import sqlite3

class Database():
    def __init__(self):
        self.conn = sqlite3.connect('database')
        self.cursor = self.conn.cursor()
        self.cursor.execute("CREATE TABLE IF NOT EXISTS estancias(id INTEGER, nombre TEXT PRIMARY KEY)")
        self.cursor.execute("CREATE TABLE IF NOT EXISTS elementos(estancia TEXT, nombre TEXT, estado INTEGER)")
        self.cursor.execute("CREATE TABLE IF NOT EXISTS tableIp(id INTEGER, ip TEXT)")
        self.cursor.execute("INSERT INTO tableIp VALUES (?,?)", (0,"prueba"))

    def insertarEstancia(self, id, nombre):
        self.cursor.execute("INSERT INTO estancias VALUES (?,?)",(id, nombre))
        self.conn.commit()
    
    def insertarElemento(self, estancia, nombre, estado):
        self.cursor.execute("INSERT INTO elementos VALUES (?,?,?)", (estancia, nombre, estado))
        self.conn.commit()
        
    def actualizarEstancia(self, nombreViejo, nombreNuevo):
        self.cursor.execute("UPDATE estancias SET nombre = ? WHERE nombre = ?", (nombreNuevo, nombreViejo))
        self.cursor.execute("UPDATE elementos SET estancia = ? WHERE estancia = ?", (nombreNuevo, nombreViejo))
        self.conn.commit()

    def actualizarElemento(self, estancia, nombreViejo, nombreNuevo):
        self.cursor.execute("UPDATE elementos SET nombre = ? WHERE estancia = ? AND nombre = ?", (nombreNuevo, estancia, nombreViejo))
        self.conn.commit()

    def actualizarEstado(self, estancia, nombre, estado):
        self.cursor.execute("UPDATE elementos SET estado = ? WHERE estancia = ? AND nombre = ?",(estado, estancia, nombre))
        self.conn.commit()

    def actualizarIP(self, ip):
        self.cursor.execute("UPDATE tableIp SET ip = ? WHERE id = 0",
                            (ip,))
        self.conn.commit()

    def eliminarEstancia(self, estancia):
        self.cursor.execute("DELETE FROM estancias WHERE nombre = ?", (estancia,))
        self.cursor.execute("DELETE FROM elementos WHERE estancia = ?", (estancia,))
        self.conn.commit()

    def eliminarElemento(self, estancia, index):
        fila = self.recuperarElemento(estancia, index)
        self.cursor.execute("DELETE FROM elementos WHERE estancia = ? AND nombre = ?", (estancia, fila[1]))
        self.conn.commit()

    def recuperarEstancia(self, index):
        self.cursor.execute("SELECT * FROM estancias")
        lista = self.cursor.fetchall()
        return lista[index]

    def recuperarElemento(self, estancia, index):
        self.cursor.execute("SELECT * FROM elementos WHERE estancia=?", (estancia,))
        lista = self.cursor.fetchall()
        return lista[index]

    def recuperarIp(self):
        self.cursor.execute("SELECT * FROM tableIp WHERE id=?", (0,))
        lista = self.cursor.fetchall()
        return lista

    def numeroEstancias(self):
        self.cursor.execute("SELECT * FROM estancias")
        lista = self.cursor.fetchall()
        return len(lista)

    def numeroElementos(self, estancia):
        self.cursor.execute("SELECT * FROM elementos WHERE estancia=?", (estancia,))
        lista = self.cursor.fetchall()
        return len(lista)

    def cerrarBD(self):
        self.cursor.close()
        self.conn.close()

    def borrarBD(self):
        self.cursor.execute("DROP TABLE IF EXISTS estancias")
        self.cursor.execute("DROP TABLE IF EXISTS elementos")
        self.cursor.execute("DROP TABLE IF EXISTS tableIp")
        self.conn.commit()