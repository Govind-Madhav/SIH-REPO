from fastapi import APIRouter, WebSocket, WebSocketDisconnect

from app.services.simulation_service import subscribe, unsubscribe

router = APIRouter()


@router.websocket("/ws")
async def ws_endpoint(websocket: WebSocket):
    await websocket.accept()
    queue = subscribe()
    try:
        while True:
            event = await queue.get()
            await websocket.send_json(event)
    except WebSocketDisconnect:
        pass
    finally:
        unsubscribe(queue)
