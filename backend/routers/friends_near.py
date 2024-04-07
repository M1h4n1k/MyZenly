from fastapi import Request, APIRouter, Depends, HTTPException
from database import crud, schemas, models
from database.loader import get_db
from sqlalchemy.orm import Session
from .dependencies import get_user as get_user_dependency


router = APIRouter(prefix='/near')


@router.get('/', response_model=list[schemas.User], status_code=200)
def get_friend_near(user: models.User = Depends(get_user_dependency), db: Session = Depends(get_db)):
    # https://ipinfo.io/json
    return crud.get_near_users(user, db)

