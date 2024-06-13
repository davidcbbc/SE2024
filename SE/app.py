from flask import Flask, request, jsonify
import sqlite3
import threading, time, socket

app = Flask(__name__)

# Helper function to get database connection
def get_db_connection():
    conn = sqlite3.connect('DBSE.db', timeout=10)  # Aumenta o timeout padrão
    conn.row_factory = sqlite3.Row
    return conn

def arduinoComm():
	arduino_ip = '192.168.175.14'
	arduino_port = 23
	message_interval = 30
	message = 'spin_message\n'
	last_time_sent = 0
	while(True):
		current_time = time.time()
		if current_time - last_time_sent >= message_interval:
			try:
				with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
					s.connect((arduino_ip, arduino_port))
					s.sendall(message.encode())
					last_time_sent = current_time
					data = s.recv(3)
					if data:
						received_value = int(data.decode())
						if received_value == -1:
							print("Rcv: Command not found")
							time.sleep(1)
							continue
						print(f"Received roullete number: {received_value}")
						manage_bets(received_value)
					else:
						print("Didn't receive any number")
					
				print("Message Sent")
			except Exception as e:
				print("Failed to sent message or received invalid")
				
		time.sleep(1)
		
		

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
    

# /stop_bets
@app.route('/stop_bets', methods=['POST'])
def stop_bets():
	wait(10)
	return
    
 # /get_past_bets endpoint
@app.route('/get_past_bets', methods=['POST'])
def get_past_bets():
	data = request.get_json()
	user_id = data.get('user_id')
	conn = get_db_connection()
	
	past_bets = conn.execute('SELECT * FROM BetHistory WHERE user_id =? ORDER BY id DESC', (user_id, )).fetchall()
	past_bets_list = []
	for pb in past_bets:
		user_data = {
			'user_id': pb['user_id'],
			'ammount': pb['ammount'], 
			'roulette_number': pb['roulette_number'], 
			'color': pb['color'], 
			'result': pb['result']
		}
		past_bets_list.append(user_data)
	conn.close()
	return jsonify(past_bets_list)

# /make_bet endpoint
@app.route('/make_bet', methods=['POST'])
def make_bet():
    data = request.get_json()
    user_id = data.get('user_id')
    amount = data.get('amount')
    roulette_number = data.get('roulette_number')
    print("Roullete_Number", roulette_number)
    conn = get_db_connection()
    wallet = conn.execute('SELECT * FROM Wallet WHERE user_id = ?', (user_id,)).fetchone()
    if wallet and (wallet['balance'] >= amount):
        new_balance = wallet['balance'] - amount
        print("new_balance: ", new_balance)
        print("user_id: ", user_id)
        conn.execute('UPDATE Wallet SET balance = ? WHERE user_id = ?', (new_balance, user_id))
        if (roulette_number == 0):
            conn.execute('INSERT INTO Bets (user_id, amount, roulette_number, color) VALUES (?, ?, ?, ?)', (user_id, amount, roulette_number, "green"))
        elif(roulette_number%2 == 1 and roulette_number < 10):
            conn.execute('INSERT INTO Bets (user_id, amount, roulette_number, color) VALUES (?, ?, ?, ?)', (user_id, amount, roulette_number, "black"))
        elif (roulette_number%2 == 0 and roulette_number < 10):
            conn.execute('INSERT INTO Bets (user_id, amount, roulette_number, color) VALUES (?, ?, ?, ?)', (user_id, amount, roulette_number, "red"))
        elif (roulette_number == 10):
            conn.execute('INSERT INTO Bets (user_id, amount, roulette_number, color) VALUES (?, ?, ?, ?)', (user_id, amount, 10, "black"))
        elif (roulette_number == 11):
            conn.execute('INSERT INTO Bets (user_id, amount, roulette_number, color) VALUES (?, ?, ?, ?)', (user_id, amount, 11, "red"))
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

#arduino_thread = threading.Thread(target=arduinoComm())
#arduino_thread.daemon = True
#arduino_thread.start()

if __name__ == '__main__':
	app.run(host="0.0.0.0", debug=True)
	
	
