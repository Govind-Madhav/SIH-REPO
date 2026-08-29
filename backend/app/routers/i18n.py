from fastapi import APIRouter, HTTPException

from app.translations import STRINGS, LANGUAGES

router = APIRouter(prefix="/api/i18n", tags=["i18n"])


@router.get("/languages")
def languages():
    return LANGUAGES


@router.get("/{lang}")
def strings(lang: str):
    if lang not in STRINGS:
        raise HTTPException(404, f"Unsupported language '{lang}'")
    return STRINGS[lang]
