from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
import os


SQLALCHEMY_DATABASE_URL = f'mysql+pymysql://root:{os.getenv("DBPASS", "rectione")}@{os.getenv("DATABASE", "127.0.0.1")}/mzenly?charset=utf8mb4'


engine = create_engine(SQLALCHEMY_DATABASE_URL, pool_recycle=3600, pool_size=5, pool_pre_ping=True)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
