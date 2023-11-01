![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F.svg?style=for-the-badge&logo=Spring-Boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F.svg?style=for-the-badge&logo=Spring-Security&logoColor=white)
![Git](https://img.shields.io/badge/git%20-%23F05033.svg?&style=for-the-badge&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
<h1 align="center">Backend сайта объявлений.</h1>

<h3>Функционал для пользователя:</h3>
<ul>

**Авторизованный как USER пользователь** может:
<li>Просматривать список всех объявлений.</li>
<li>Просматривать каждое объявление.</li>
<li>Создавать объявление.</li>
<li>Редактировать и удалять свое объявление.</li>
<li>Просматривать все комментарии к объявлениям.</li>
<li>Создавать комментарии к любым объявлениям.</li>
<li>Редактировать/удалять свои комментарии.</li>

**Авторизованный как ADMIN пользователь в дополнении** может:
<li>Редактировать и удалять любые комментарии.</li>
<li>Редактировать и удалять любые объявления.</li>

</ul>

**Для запуска нужно:**
1. Клонировать проект и настроить значения в файле **[application.properties](src/main/resources/application.properties)**</li>
2. Скачать **[Docker](https://www.docker.com)** и запустить его.
3. Скачать и запустить Docker образ с помощью команды ```docker run -p 3000:3000 ghcr.io/bizinmitya/front-react-avito:latest```.
4. Запустить метод **main** программы.
5. После этого будет доступен функционал сайта  http://localhost:3000 и Swagger UI   http://localhost:8080/swagger-ui/index.html#.

<h3>Участники проекта:</h3>
<ul>
<li> Евгений Белов</li>
<li> Лапутин Александр</li>
<li> Катерина ... </li>


</ul>