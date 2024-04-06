from fastapi import Request, APIRouter, Depends, HTTPException
from database import crud, schemas
from database.loader import get_db
from sqlalchemy.orm import Session
from math import sin, cos, sqrt, atan2, radians


router = APIRouter(prefix='/near')


@router.get('/', response_model=list[schemas.User], status_code=200)
def get_friend_near(user_id: int, db: Session = Depends(get_db)):
    # https://ipinfo.io/json
    return crud.get_near_users(db)

