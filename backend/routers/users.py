from fastapi import Request, APIRouter, Depends, HTTPException
from database import crud, schemas, models
from database.loader import get_db
from sqlalchemy.orm import Session


router = APIRouter(prefix='/users')


@router.post('/', response_model=schemas.User, status_code=201)
def create_user(user: schemas.UserCreate, db: Session = Depends(get_db)):
    return crud.create_user(db, models.User(**user.dict()))


@router.get(
    '/{user_id}',
    response_model=schemas.User,
    status_code=200,
    responses={
        403: {'description': 'Not implemented yet'},  # TODO
        404: {'description': 'You cannot be friends with yourself'}
    }
)
def get_user(user_id: int, db: Session = Depends(get_db)):
    user = crud.get_user(db, user_id)
    if not user:
        raise HTTPException(status_code=404, detail='User not found')
    return user
