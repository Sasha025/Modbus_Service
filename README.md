# Modbus Service

## Требования
- Docker
- Эмулятор Modbus: ModbusPal

---

## Запуск

1. Запустите эмулятор ModbusPal и настройте устройства/регистры.
2. Перейдите в каталог проекта, где находится `docker-compose.yml`.
3. Соберите и запустите контейнеры:

```
docker-compose up -d --build
```
## Просмотр таблицы `Measurement` через pgAdmin

1. Откройте браузер и перейдите на: [http://localhost:5050](http://localhost:5050)
2. Войдите с учётными данными:

- Email: admin@admin.com
- Password: admin


3. Подключитесь к серверу Postgres:

- Host: postgres
- User: demo
- Password: demo

4. В разделе **Tables** найдите таблицу `measurement` и просматривайте данные.

---

## Просмотр логов приложения

Чтобы видеть, какие значения приложение читает из регистров и сохраняет в БД, используйте команду:

```
docker-compose logs -f app
```
