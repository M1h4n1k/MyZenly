FROM python:3.11

WORKDIR /app

ENV DBPASS=passwd
ENV DATABASE=host.docker.internal

EXPOSE 84

COPY ./requirements.txt /app/requirements.txt

RUN pip install --no-cache-dir --upgrade -r /app/requirements.txt

COPY . .

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "84"]
