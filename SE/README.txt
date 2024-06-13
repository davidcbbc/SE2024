!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
IGNORAR FICHEIROS: .l.         .l.           .l.
templates                 .l.       .l.
program.py           .l.       .l.        .l.
iniciar.txt      .l.                       .l.
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
Instalar SQLite sqlite3
Instalar Postman

ver vídeo: https://www.youtube.com/watch?v=zcYMB-uXKNs&t=523s&ab_channel=NazmusNasir

depois de seguir os passos do vídeo:
(alguns comandos/passos do vídeo)
comandos no terminal da pasta
$ . venv/bin/activate
$ export FLASK_APP=app.py
$ flask run --debug --port=5001 (porta não importa pode ser qualquer numero, talvez)
---------------------------------------------------------------------------------------------------------------------------------
o meu terminal:
---------------------------------------------------------------------------------------------------------------------------------
-->root@DESKTOP-IN9VUFF:/mnt/c/Users/bruno/OneDrive/Ambiente de Trabalho/SE# ls
piApp

-->root@DESKTOP-IN9VUFF:/mnt/c/Users/bruno/OneDrive/Ambiente de Trabalho/SE# cd piApp/

-->root@DESKTOP-IN9VUFF:/mnt/c/Users/bruno/OneDrive/Ambiente de Trabalho/SE/piApp# ls
app.py  DBSE.db  DBSE.db.sql  DBSE.sqbpro  iniciar.txt  program.py  __pycache__  templates  venv

-->root@DESKTOP-IN9VUFF:/mnt/c/Users/bruno/OneDrive/Ambiente de Trabalho/SE/piApp# . venv/bin/activate

-->(venv) root@DESKTOP-IN9VUFF:/mnt/c/Users/bruno/OneDrive/Ambiente de Trabalho/SE/piApp# export FLASK_APP=app.py

-->(venv) root@DESKTOP-IN9VUFF:/mnt/c/Users/bruno/OneDrive/Ambiente de Trabalho/SE/piApp# flask run --debug --port=5001
 * Serving Flask app 'app.py'
 * Debug mode: on
WARNING: This is a development server. Do not use it in a production deployment. Use a production WSGI server instead.
 * Running on http://127.0.0.1:5001
Press CTRL+C to quit
 * Restarting with stat
 * Debugger is active!
 * Debugger PIN: 215-349-524
---------------------------------------------------------------------------------------------------------------------------------
depois, abrir postman:
meter http://127.0.0.1:5001/signup
ou                         /login
ou                         /add_balance
ou			   /make_bet
ou			   /get_my_data

clicar em Body e em raw, selecionar JSON (print em SE/print.png)
para ver a base de dados: abrir DB Browser(SQLite), clicar em open database, selecionar o ficheiro DBSE.db. ir a browse data e ver os dados. para atualizar os dados clicar no botão de atualizar msm em baixo do browse data (print em SE/print2.png)
==================================================================================================================================
/signup
{
    "name":"jaoo",
    "email":"ko@ok.com",
    "password": "123"
}
==================================================================================================================================
/login
{
    "name":"jaoo",
    "password": "123"
}
==================================================================================================================================
/add_balance
{
    "users_id": 3,
    "amount": 20   
}
==================================================================================================================================
/make_bet
{
    "users_id": 3,
    "amount": 20,
    "RoulletNumber": 123
}
==================================================================================================================================
/get_my_data
{
    "users_id": 3
}

==================================================================================================================================
PARA TESTAR CLICAR EM SEND

opa ya é isso acho eu





