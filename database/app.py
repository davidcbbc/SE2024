from flask import Flask, request, jsonify
import sqlite3

app = Flask(__name__)

# Helper function to get database connection
def get_db_connection():
    conn = sqlite3.connect('DBSE.db', timeout=10)  # Aumenta o timeout padrão
    conn.row_factory = sqlite3.Row
    return conn

# /login endpoint
@app.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    name = data.get('name')
    password = data.get('password')
    conn = get_db_connection()
    user = conn.execute('SELECT * FROM Users WHERE name = ? AND password = ?', (name, password)).fetchone()
    wallet = conn.execute('SELECT * FROM Wallet WHERE user_id = ?', (user['id'],)).fetchone()
    conn.close()
    if user:
        user_data = {
            'user_id': user['id'],
            'name': user['name'],
            'balance': wallet['balance']
        }
        return jsonify(user_data)
    else:
        return jsonify(user_id=0)

# /signup endpoint
@app.route('/signup', methods=['POST'])
def signup():
    data = request.get_json()
    name = data.get('name')
    password = data.get('password')
    conn = get_db_connection()
    existing_user = conn.execute('SELECT * FROM Users WHERE name = ?', (name,)).fetchone()
    if existing_user:
        conn.close()
        return "User já existe!"
    conn.execute('INSERT INTO Users (name, password) VALUES (?, ?)', (name, password))
    conn.commit()
    user_id = conn.execute('SELECT id FROM Users WHERE name = ?', (name,)).fetchone()['id']
    conn.execute('INSERT INTO Wallet (user_id, balance) VALUES (?, ?)', (user_id, 0.0))
    conn.commit()
    conn.close()
    return "User criado!"

# /add_balance endpoint
@app.route('/add_balance', methods=['POST'])
def add_balance():
    data = request.get_json()
    user_id = data.get('user_id')
    amount = data.get('amount')
    conn = get_db_connection()
    wallet = conn.execute('SELECT * FROM Wallet WHERE user_id = ?', (user_id,)).fetchone()
    if wallet:
        new_balance = wallet['balance'] + amount
        conn.execute('UPDATE Wallet SET balance = ? WHERE user_id = ?', (new_balance, user_id))
        conn.commit()
        conn.close()
        return "Balance added successfully"
    conn.close()
    return "User not found", 404

# /make_bet endpoint
@app.route('/make_bet', methods=['POST'])
def make_bet():
    data = request.get_json()
    user_id = data.get('user_id')
    amount = data.get('amount')
    roulette_number = data.get('roulette_number')
    conn = get_db_connection()
    wallet = conn.execute('SELECT * FROM Wallet WHERE user_id = ?', (user_id,)).fetchone()
    if wallet and (wallet['balance'] >= amount):
        new_balance = wallet['balance'] - amount
        print("new_balance: ", new_balance)
        print("user_id: ", user_id)
        conn.execute('UPDATE Wallet SET balance = ? WHERE user_id = ?', (new_balance, user_id))
        if (roulette_number == 0):
            conn.execute('INSERT INTO Bets (user_id, amount, roulette_number, color) VALUES (?, ?, ?, ?)', (user_id, amount, roulette_number, "green"))
        elif(roulette_number%2 == 1 and roulette_number < 37):
            conn.execute('INSERT INTO Bets (user_id, amount, roulette_number, color) VALUES (?, ?, ?, ?)', (user_id, amount, roulette_number, "black"))
        elif (roulette_number%2 == 0 and roulette_number < 37):
            conn.execute('INSERT INTO Bets (user_id, amount, roulette_number, color) VALUES (?, ?, ?, ?)', (user_id, amount, roulette_number, "red"))
        elif (roulette_number == 37):
            conn.execute('INSERT INTO Bets (user_id, amount, roulette_number, color) VALUES (?, ?, ?, ?)', (user_id, amount, None, "black"))
        elif (roulette_number == 38):
            conn.execute('INSERT INTO Bets (user_id, amount, roulette_number, color) VALUES (?, ?, ?, ?)', (user_id, amount, None, "red"))
        conn.commit()
        conn.close()
        return "Bet placed"
    conn.close()
    return "Não tem dinheiro suficiente."

# /get_my_data endpoint
@app.route('/get_my_data', methods=['POST'])
def get_my_data():
    data = request.get_json()
    user_id = data.get('user_id')
    conn = get_db_connection()
    user = conn.execute('SELECT * FROM Users WHERE id = ?', (user_id,)).fetchone()
    wallet = conn.execute('SELECT * FROM Wallet WHERE user_id = ?', (user_id,)).fetchone()
    if user and wallet:
        user_data = {
            'name': user['name'],
            'password': user['password'],
            'balance': wallet['balance']
        }
        conn.close()
        return jsonify(user_data)
    conn.close()
    return "User not found", 404

if __name__ == '__main__':
    app.run(debug=True)
