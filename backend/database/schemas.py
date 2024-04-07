from pydantic import BaseModel
from datetime import datetime


class UserUpdateIn(BaseModel):
    nickname: str | None = None
    place: str | None = None
    latitude: float | None = None
    longitude: float | None = None
    visible: bool | None = None


class UserUpdate(UserUpdateIn):
    id: int

    class Config:
        from_attributes = True


class User(BaseModel):
    id: int
    nickname: str
    place: str
    latitude: float
    longitude: float
    last_update: datetime

    class Config:
        from_attributes = True


class Profile(BaseModel):
    id: int
    nickname: str
    place: str | None = None
    latitude: float | None = None
    longitude: float | None = None
    last_update: datetime
    visible: bool
    friends: list[User]
    requests: list[User]
    near: list[User] | None
