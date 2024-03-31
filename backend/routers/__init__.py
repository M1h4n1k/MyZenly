from fastapi import APIRouter
from .friends import router as friends_router
from .friend_requests import router as friend_requests_router
from .users import router as users_router


router = APIRouter(prefix='/api')
router.include_router(users_router)
router.include_router(friends_router)
router.include_router(friend_requests_router)

__all__ = ['router']
