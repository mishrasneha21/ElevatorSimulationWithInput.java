import mysql.connector

# ---------------- DATABASE CONNECTION ----------------

db = mysql.connector.connect(
    host="localhost",
    user="root",        # change if needed
    password="root"     # change if needed
)

cursor = db.cursor()

# ---------------- CREATE DATABASE & TABLES ----------------

cursor.execute("CREATE DATABASE IF NOT EXISTS student_result_db")
cursor.execute("USE student_result_db")

cursor.execute("""
CREATE TABLE IF NOT EXISTS students (
    roll_no INT PRIMARY KEY,
    name VARCHAR(50),
    course VARCHAR(50),
    semester INT
)
""")

cursor.execute("""
CREATE TABLE IF NOT EXISTS marks (
    roll_no INT,
    subject VARCHAR(50),
    marks INT,
    FOREIGN KEY (roll_no) REFERENCES students(roll_no)
)
""")

print("Database and tables are ready!\n")

# ---------------- FUNCTIONS ----------------

def add_student():
    roll = int(input("Enter Roll No: "))
    name = input("Enter Name: ")
    course = input("Enter Course: ")
    sem = int(input("Enter Semester: "))

    query = "INSERT INTO students VALUES (%s, %s, %s, %s)"
    values = (roll, name, course, sem)

    try:
        cursor.execute(query, values)
        db.commit()
        print("Student added successfully!\n")
    except:
        print("Error: Roll number already exists!\n")


def add_marks():
    roll = int(input("Enter Roll No: "))
    subjects = int(input("Enter number of subjects: "))

    for i in range(subjects):
        subject = input("Enter Subject Name: ")
        marks = int(input("Enter Marks: "))

        query = "INSERT INTO marks VALUES (%s, %s, %s)"
        values = (roll, subject, marks)
        cursor.execute(query, values)

    db.commit()
    print("Marks added successfully!\n")


def calculate_result(roll):
    cursor.execute("SELECT marks FROM marks WHERE roll_no = %s", (roll,))
    data = cursor.fetchall()

    if not data:
        print("No marks found!\n")
        return

    total = sum(mark[0] for mark in data)
    percentage = total / len(data)

    if percentage >= 75:
        grade = "A"
        status = "Pass"
    elif percentage >= 60:
        grade = "B"
        status = "Pass"
    elif percentage >= 45:
        grade = "C"
        status = "Pass"
    else:
        grade = "F"
        status = "Fail"

    print("\n------ RESULT ------")
    print("Total Marks:", total)
    print("Percentage:", percentage)
    print("Grade:", grade)
    print("Result:", status)
    print("--------------------\n")


def view_student():
    roll = int(input("Enter Roll No: "))

    cursor.execute("SELECT * FROM students WHERE roll_no = %s", (roll,))
    student = cursor.fetchone()

    if not student:
        print("Student not found!\n")
        return

    print("\nStudent Details")
    print("Roll No:", student[0])
    print("Name:", student[1])
    print("Course:", student[2])
    print("Semester:", student[3])

    cursor.execute("SELECT subject, marks FROM marks WHERE roll_no = %s", (roll,))
    records = cursor.fetchall()

    print("\nSubjects & Marks:")
    for sub in records:
        print(sub[0], ":", sub[1])

    calculate_result(roll)


# ---------------- MAIN MENU ----------------

while True:
    print("===== STUDENT RESULT MANAGEMENT SYSTEM =====")
    print("1. Add Student")
    print("2. Add Marks")
    print("3. View Student Result")
    print("4. Exit")

    choice = input("Enter your choice: ")

    if choice == '1':
        add_student()
    elif choice == '2':
        add_marks()
    elif choice == '3':
        view_student()
    elif choice == '4':
        print("Exiting... Thank you!")
        break
    else:
        print("Invalid choice! Try again.\n")
