Для локального запуска необходимо запустить БД

docker run --name postgres \
-e POSTGRES_DB=app_db \
-e POSTGRES_USER=app_user \
-e POSTGRES_PASSWORD=app_password \
-p 5432:5432 \
-d postgres:16