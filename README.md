# java-shareit

<p>
  <img src="https://img.shields.io/badge/Java-red" />
  <img src="https://img.shields.io/badge/Spring%20boot-light green" />
  <img src="https://img.shields.io/badge/Maven-yellow" />
  <img src="https://img.shields.io/badge/Hibernate-light blue" />
  <img src="https://img.shields.io/badge/JPA-purple" />
  <img src="https://img.shields.io/badge/PostgreSQL-blue" />
  <img src="https://img.shields.io/badge/Lombok-orange" />
  <img alt="Docker" src="https://img.shields.io/badge/-Docker-46a2f1?style=flat-square&logo=docker&logoColor=white" />
</p>

# О проекте
Веб-сервис предоставления вещей во временное пользование

№Микросервисы

- Gateway Внешнее API для валидации запросов пользователя
- Server Содержит бизнес-логику приложения

Каждый микросервис может быть запущен в своем докер-контейнере

# Функциональность

- User: Регистрация, обновление данных и получения списка пользователей
- Item: Добавление, обновление данных и поиск по ключевому слову вещей предоставляемых в аренду
- Booking: Создание заявок на аренду вещей. А так же возможность получения заявок и их дальнейшей обработки
- Request: Возможность размещения запроса вещей, которые еще не представлены на площадке, как следствие возможность размещения вещей в качестве ответа на запрос.
- Comment: Возможность оставить отзыв о ранее взятой в аренду вещи