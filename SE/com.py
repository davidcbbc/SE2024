import time
import sqlite3
import socket

def arduinoComm():
    arduino_ip = '192.168.175.14'
    arduino_port = 23
    message_interval = 45
    message_interval_in_error = 45
    message = "spin_message\n"
   
    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.connect((arduino_ip, arduino_port))
            print("Connected Successfully")
            while True:
                try:
                    s.sendall(message.encode())
                    stop_bets()
                    data = s.recv(3)[:1]
                    print("ArduinoData: ", data)
                    print(data)
               
                    if data:
                        received_value = int(data.decode())
                        if received_value == -1:
                            time.sleep(message_interval_in_error)
                            continue
                        print(f"Received Roulette Number: {received_value}")
                        manage_bets(data.decode())
                    else:
                        print("Didn't receive any number")
               
                    time.sleep(message_interval)  
                    print("Sleep Sent to Arduino")
                except Exception as e:
                    print("Failed to send message: ", e)
    except Exception as e:
        print("Failed connection: ", e)


def get_db_connection():
    conn = sqlite3.connect('DBSE.db', timeout=10)
    conn.row_factory = sqlite3.Row
    return conn

def manage_bets(roulette_value):
    print("ola")
    roulette_value = int(roulette_value)
    color_map = {
        "black": [1, 3, 5, 7, 9],
        "red": [2, 4, 6, 8],
        "green": [0]
    }
   
    if roulette_value in color_map["red"]:
        roulette_value_color = "red"
    elif roulette_value in color_map["black"]:
        roulette_value_color = "black"
    else:
        roulette_value_color = "green"
   
    conn = get_db_connection()
    cursor = conn.cursor()
   
    print("Chegou Aqui")
   
    # Process bets on the color
    if roulette_value_color in ["red", "black"]:
        cursor.execute('SELECT * FROM Bets WHERE color = ? AND roulette_number IN (10,11);', (roulette_value_color,))
        color_bets = cursor.fetchall()
       
        print(color_bets)
       
        for bet in color_bets:
            print("Bet: ", bet)
            print("color2 = ", roulette_value_color)
            row_id, user_id, amount, roulette_number, color = bet[0], bet[1], bet[2], bet[3], bet[4]
            gained_value = amount * 2 + amount
            conn.execute('UPDATE Wallet SET balance = balance + ? WHERE user_id = ?;', (gained_value, user_id))
            conn.execute('INSERT INTO BetHistory (user_id, ammount, roulette_number, color, result) VALUES (?, ?, ?, ?, ?);', (user_id, amount, roulette_number, color, "WIN"))
            conn.execute('DELETE FROM Bets WHERE id = ?;', (row_id,))
    
    conn.execute('INSERT INTO BetHistory (user_id, ammount, roulette_number, color, result) SELECT user_id, amount, roulette_number, color, "LOSS" FROM Bets WHERE roulette_number IN (10,11);')        
    conn.execute('DELETE FROM Bets WHERE roulette_number IN (10, 11);')
   
    # Insert all remaining losing bets into BetHistory and delete them from Bets
    # conn.execute('INSERT INTO BetHistory (user_id, ammount, roulette_number, color, result) SELECT user_id, amount, roulette_number, color, "LOSS" FROM Bets;')
    # conn.execute('DELETE FROM Bets;')
    
    # Process bets on the specific number
    cursor.execute('SELECT * FROM Bets WHERE roulette_number=?;', (roulette_value,))
    number_bets = cursor.fetchall()
   
    for bet in number_bets:
        print("color1 = ", roulette_value_color)
        row_id, user_id, amount, roulette_number, color = bet[0], bet[1], bet[2], bet[3], bet[4]
        gained_value = amount * 10 + amount
        conn.execute('UPDATE Wallet SET balance = balance + ? WHERE user_id = ?;', (gained_value, user_id))
        conn.execute('INSERT INTO BetHistory (user_id, ammount, roulette_number, color, result) VALUES (?, ?, ?, ?, ?);', (user_id, amount, roulette_number, color, "WIN"))
        conn.execute('DELETE FROM Bets WHERE id = ?;', (row_id,))
   
    # Insert all losing number bets into BetHistory and delete them from Bets
    conn.execute('INSERT INTO BetHistory (user_id, ammount, roulette_number, color, result) SELECT user_id, amount, roulette_number, color, "LOSS" FROM Bets WHERE roulette_number != ?;', (roulette_value,))
    conn.execute('DELETE FROM Bets;')
    
    
    conn.commit()
    conn.close()

def stop_bets():
    request = 0
    return

arduinoComm()
