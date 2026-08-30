import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart';
import 'offline_event.dart';

class OfflineQueueService {
  static Database? _database;

  Future<Database> get database async {
    if (_database != null) return _database!;
    _database = await _initDatabase();
    return _database!;
  }

  Future<Database> _initDatabase() async {
    final dbPath = await getDatabasesPath();
    final path = join(dbPath, 'ner_mobile_offline_queue.db');

    return await openDatabase(
      path,
      version: 1,
      onCreate: (db, version) async {
        await db.execute('''
          CREATE TABLE offline_events (
            clientEventId TEXT PRIMARY KEY,
            eventType TEXT NOT NULL,
            payloadJson TEXT NOT NULL,
            createdAt TEXT NOT NULL,
            syncStatus TEXT NOT NULL,
            retryCount INTEGER NOT NULL,
            lastError TEXT
          )
        ''');
      },
    );
  }

  Future<void> enqueue(OfflineEvent event) async {
    final db = await database;
    await db.insert(
      'offline_events',
      event.toMap(),
      conflictAlgorithm: ConflictAlgorithm.replace,
    );
  }

  Future<List<OfflineEvent>> getPendingEvents() async {
    final db = await database;
    final maps = await db.query(
      'offline_events',
      where: 'syncStatus = ? OR syncStatus = ?',
      whereArgs: ['PENDING', 'FAILED'],
      orderBy: 'createdAt ASC',
    );
    return maps.map((m) => OfflineEvent.fromMap(m)).toList();
  }

  Future<void> updateStatus(String clientEventId, String status, {String? error}) async {
    final db = await database;
    await db.update(
      'offline_events',
      {
        'syncStatus': status,
        if (error != null) 'lastError': error,
      },
      where: 'clientEventId = ?',
      whereArgs: [clientEventId],
    );
  }

  Future<void> markSynced(String clientEventId) async {
    await updateStatus(clientEventId, 'SYNCED');
  }
}
