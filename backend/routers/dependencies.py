from fastapi import Header, Request, Depends, HTTPException
from sqlalchemy.orm import Session
from database.loader import get_db
from database import crud


async def get_user(authorization: str = Header(), db: Session = Depends(get_db)):
    user = crud.get_user_by_token(db, authorization)
    if not user:
        raise HTTPException(status_code=403, detail='Forbidden')
    return user
