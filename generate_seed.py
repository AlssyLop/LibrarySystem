import random
import datetime

NUM_USERS = 978
NUM_AUTHORS = 1256
NUM_BOOKS = 4751
NUM_ACTIVE_LOANS = 676
NUM_RETURNED_LOANS = 2115

with open("seed_data.sql", "w", encoding="utf-8") as f:
    f.write("SET FOREIGN_KEY_CHECKS = 0;\n")
    f.write("TRUNCATE TABLE loans;\n")
    f.write("TRUNCATE TABLE books;\n")
    f.write("TRUNCATE TABLE authors;\n")
    f.write("TRUNCATE TABLE users;\n")
    f.write("SET FOREIGN_KEY_CHECKS = 1;\n\n")

    # Generate Users
    f.write("-- Insert Users\n")
    users_values = []
    for i in range(1, NUM_USERS + 1):
        name = f"Usuario_{i}"
        email = f"usuario{i}@example.com"
        phone = f"555-{random.randint(1000, 9999)}"
        users_values.append(f"('{name}', '{email}', '{phone}', 1)")
        if len(users_values) == 500 or i == NUM_USERS:
            f.write("INSERT INTO users (name, email, phone, activo) VALUES\n" + ",\n".join(users_values) + ";\n")
            users_values = []

    # Generate Authors
    f.write("-- Insert Authors\n")
    authors_values = []
    nationalities = ["República Dominicana", "Afganistán", "Bolivia", "Colombia", "Chile", "Angola"]
    for i in range(1, NUM_AUTHORS + 1):
        name = f"Autor_{i}"
        nationality = random.choice(nationalities)
        authors_values.append(f"('{name}', '{nationality}')")
        if len(authors_values) == 500 or i == NUM_AUTHORS:
            f.write("INSERT INTO authors (name, nationality) VALUES\n" + ",\n".join(authors_values) + ";\n")
            authors_values = []

    # Generate Books
    f.write("-- Insert Books\n")
    books_values = []
    for i in range(1, NUM_BOOKS + 1):
        title = f"Libro_{i} - {random.choice(['El Misterio', 'La Historia', 'Cuentos', 'Manual', 'Cronicas'])}"
        isbn = f"{random.randint(100, 999)}-{random.randint(100, 999)}-{random.randint(100, 999)}"
        year = random.randint(1900, 2024)
        id_author = random.randint(1, NUM_AUTHORS)
        books_values.append(f"('{title}', '{isbn}', {year}, {id_author})")
        if len(books_values) == 500 or i == NUM_BOOKS:
            f.write("INSERT INTO books (title, isbn, year, id_author) VALUES\n" + ",\n".join(books_values) + ";\n")
            books_values = []

    # Generate Loans
    f.write("-- Insert Loans\n")
    loans_values = []
    
    def random_date(start_year, end_year):
        year = random.randint(start_year, end_year)
        month = random.randint(1, 12)
        day = random.randint(1, 28)
        return datetime.date(year, month, day)

    for i in range(1, NUM_ACTIVE_LOANS + 1):
        id_user = random.randint(1, NUM_USERS)
        id_book = random.randint(1, NUM_BOOKS)
        loan_date = random_date(2023, 2024)
        loans_values.append(f"('{loan_date}', NULL, {id_user}, {id_book}, 0)")
        if len(loans_values) == 500 or i == NUM_ACTIVE_LOANS:
            f.write("INSERT INTO loans (loan_date, return_date, id_user, id_book, returned) VALUES\n" + ",\n".join(loans_values) + ";\n")
            loans_values = []

    loans_values = []
    for i in range(1, NUM_RETURNED_LOANS + 1):
        id_user = random.randint(1, NUM_USERS)
        id_book = random.randint(1, NUM_BOOKS)
        loan_date = random_date(2020, 2023)
        return_date = loan_date + datetime.timedelta(days=random.randint(1, 30))
        loans_values.append(f"('{loan_date}', '{return_date}', {id_user}, {id_book}, 1)")
        if len(loans_values) == 500 or i == NUM_RETURNED_LOANS:
            f.write("INSERT INTO loans (loan_date, return_date, id_user, id_book, returned) VALUES\n" + ",\n".join(loans_values) + ";\n")
            loans_values = []

print("SQL seed file generated successfully as seed_data.sql")
