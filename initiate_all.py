import subprocess
import signal
import os
import sys
import time

# List to hold the subprocesses
processes = []

def start_processes():
    global processes
    # Start lxterminals and sqlitebrowser
    processes.append(subprocess.Popen(['lxterminal', '--command=bash -c "cd camera; python3 app.py"'], preexec_fn=os.setsid))
    processes.append(subprocess.Popen(['lxterminal', '--command=bash -c "cd SE; . venv/bin/activate; export FLASK_APP=app.py; python3 app.py"'], preexec_fn=os.setsid))
    processes.append(subprocess.Popen(['lxterminal', '--command=bash -c "cd SE; python3 com.py"'], preexec_fn=os.setsid))
    processes.append(subprocess.Popen(['sqlitebrowser', './SE/DBSE.db'], preexec_fn=os.setsid))

def cleanup_processes():
    for process in processes:
        try:
            # Send SIGTERM to the process group
            os.killpg(os.getpgid(process.pid), signal.SIGTERM)
        except Exception as e:
            print(f"Error terminating process {process.pid}: {e}")
    time.sleep(1)  # Give some time for processes to terminate

    for process in processes:
        if process.poll() is None:  # Process is still running
            try:
                # Send SIGKILL to the process group
                os.killpg(os.getpgid(process.pid), signal.SIGKILL)
            except Exception as e:
                print(f"Error killing process {process.pid}: {e}")
    for process in processes:
        process.wait()  # Ensure processes have terminated

def signal_handler(sig, frame):
    print("Caught interrupt signal, terminating processes...")
    cleanup_processes()
    sys.exit(0)

if __name__ == "__main__":
    # Register the signal handler for clean termination on Ctrl+C
    signal.signal(signal.SIGINT, signal_handler)

    # Start the processes
    start_processes()

    # Keep the main thread running
    try:
        while True:
            signal.pause()  # Wait for signals
    except KeyboardInterrupt:
        # Should not reach here as we are handling SIGINT explicitly
        pass
