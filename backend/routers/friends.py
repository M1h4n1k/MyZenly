from fastapi import Request, APIRouter, Depends, HTTPException
from database import crud, schemas
from database.loader import get_db
from sqlalchemy.orm import Session
from .friends_requests import router as friend_requests_router
from .friends_near import router as friend_near_router


router = APIRouter(prefix='/friends')
router.include_router(friend_requests_router)
router.include_router(friend_near_router)


@router.get('/', response_model=list[schemas.User])
def get_friends(user_id: int, db: Session = Depends(get_db)):
    return crud.get_friends(db, user_id)


@router.post(
    '/',
    status_code=201,
    responses={
        400: {'description': 'You cannot be friends with yourself'},
        403: {'description': 'Friend request not found'},
        404: {'description': 'User not found'},
    }
)
def add_friend(user1_id: int, user2_id: int, db: Session = Depends(get_db)):
    if user1_id == user2_id:
        raise HTTPException(status_code=400, detail='You cannot be friends with yourself')
    if not crud.get_user(db, user1_id) or not crud.get_user(db, user2_id):
        raise HTTPException(status_code=404, detail='User not found')
    if not crud.get_request(db, user2_id, user1_id):
        raise HTTPException(status_code=403, detail='Friend request not found')

    crud.add_friend(db, user1_id, user2_id)
    return 'OK'


@router.delete('/')
def delete_friend(user1_id: int, user2_id: int, db: Session = Depends(get_db)):
    crud.delete_friend(db, user1_id, user2_id)
    return 'OK'
