import socket
import sys
import threading
from tkinter import *
from tkinter import messagebox
from tkinter import ttk

from Database import Database

HOST = socket.gethostname()
PORT = 8888

class Servidor():

    def __init__(self):
        servidor = threading.Thread(target=self.iniciarServidor)
        servidor.start()
        self.framesPestannas = []
        self.estancias = {}
        self.iniciarInterfaz()

    def iniciarInterfaz(self):
        self.ventana = Tk()
        self.ventana.title('Servidor')
        self.ventana.configure(bg='black')
        self.centrarVentana(self.ventana, 600, 600)
        self.notebook = ttk.Notebook(self.ventana)
        self.notebook.pack(fill='both', expand='yes')
        self.bombillaApagada = PhotoImage(file="imagenes/luzapagada2.png")
        self.bombillaEncendida = PhotoImage(file="imagenes/luzencendida.png")
        self.cama = PhotoImage(file="imagenes/cama.png")
        self.salon = PhotoImage(file="imagenes/salon.png")
        self.cocina = PhotoImage(file="imagenes/cocina.png")
        self.wc = PhotoImage(file="imagenes/wc.png")
        self.imagenesEstancias = {0: self.cama, 1: self.salon, 2: self.cocina, 3: self.wc}

        db = Database()
        for i in range(db.numeroEstancias()):
            estancia = db.recuperarEstancia(i)
            contenedor = Frame(self.notebook)
            self.notebook.add(child=contenedor, text=estancia[1], image=self.imagenesEstancias[estancia[0]],
                              compound=LEFT)
            self.framesPestannas.append(contenedor)
            self.estancias[estancia[1]] = []
            for j in range(db.numeroElementos(estancia[1])):
                elemento = db.recuperarElemento(estancia[1], j)
                label = Label(contenedor, text=elemento[1], compound=TOP)
                if elemento[2] == 0:
                    label['image'] = self.bombillaApagada
                else:
                    label['image'] = self.bombillaEncendida
                label.grid()
                self.estancias[estancia[1]].append(label)
        db.cerrarBD()
        self.ventana.mainloop()

    def centrarVentana(self, ventana, width, height):
        # cogemos el ancho y el alto de la pantalla
        screen_width = self.ventana.winfo_screenwidth()
        screen_height = self.ventana.winfo_screenheight()
        # calculamos las coordenadas para representar la pantalla
        x = (screen_width / 2) - (width / 2)
        y = (screen_height / 2) - (height / 2)
        ventana.geometry('%dx%d+%d+%d' % (width, height, x, y))

    def leerDatos(self, datos):
        db = Database()
        if datos[0] == '+':
            if datos[1] == 'E':
                contenedor = Frame(self.notebook)
                self.notebook.add(child=contenedor,text=datos[3:], compound=LEFT , image=self.imagenesEstancias[int(datos[2:3])], padding = 5)
                self.framesPestannas.append(contenedor)
                self.estancias[datos[3:]]=[]
                db.insertarEstancia(datos[2:3], datos[3:])
            else:
                lista = self.estancias[self.notebook.tab(int(datos[2:3]),'text')]
                label = Label(self.framesPestannas[int(datos[2:3])], text=datos[3:], compound=TOP, image=self.bombillaApagada)
                label.grid()
                lista.append(label)
                self.estancias[self.notebook.tab(datos[2:3],'text')] = lista
                db.insertarElemento(self.notebook.tab(int(datos[2:3]),'text'), datos[3:], 0)
        else:
            if datos[0] == '-':
                if datos[1] == 'E':
                    del self.estancias[self.notebook.tab(int(datos[2:]), 'text')]
                    del self.framesPestannas[int(datos[2:])]
                    db.eliminarEstancia(self.notebook.tab(int(datos[2:]), 'text'))
                    self.notebook.forget(datos[2:])
                else:
                    lista = self.estancias[self.notebook.tab(int(datos[2:3]), 'text')]
                    lista[int(datos[3:4])].destroy()
                    lista.remove(lista[int(datos[3:4])])
                    db.eliminarElemento(self.notebook.tab(int(datos[2:3]), 'text'), int(datos[3:4]))
            else:
                if datos[0] == '*':
                    if datos[1] == 'E':
                        lista = self.estancias[self.notebook.tab(int(datos[2:3]), 'text')]
                        self.estancias[datos[3:]] = lista
                        del self.estancias[self.notebook.tab(int(datos[2:3]), 'text')]
                        db.actualizarEstancia(self.notebook.tab(int(datos[2:3]), 'text'), datos[3:])
                        self.notebook.tab(int(datos[2:3]), text=datos[3:])
                    else:
                        lista = self.estancias[self.notebook.tab(int(datos[2:3]), 'text')]
                        label = lista[int(datos[3:4])]
                        db.actualizarElemento(self.notebook.tab(int(datos[2:3]), 'text'), label['text'], datos[4:])
                        label['text'] = datos[4:]
                else:
                    if datos[0] == '#':
                        bombilla = db.recuperarElemento(datos[3:],int(datos[2]))
                        db.actualizarEstado(datos[3:],bombilla[1],int(datos[1]))
                        label = self.estancias[datos[3:]][int(datos[2])]
                        if datos[1] == '1':
                            label['image'] = self.bombillaEncendida
                        else:
                            label['image'] = self.bombillaApagada
                    else:
                        print('No entra en ninguno')
        db.cerrarBD()

    def iniciarServidor(self):
        # Servidor socket
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        print("Socket creado correctamente")

        try:
            s.bind((HOST, PORT))
        except socket.error as err:
            print("Bind Failed, Error Code: " + str(err[0]) + ", Message: " + err[1])
            sys.exit()

        s.listen(10)
        print("Socket está ahora escuchando")

        while True:
            # Esperando conexion
            print('Esperando para conectarse')
            connection, client_address = s.accept()

            try:
                print('Conexión desde' + str(client_address))

                # Recibe los datos en trozos y reetransmite
                while True:
                    data = connection.recv(1024)
                    if data:
                        print('Recibido el mensaje ' + "'" + data.decode() + "'")
                        print('Enviando mensaje de vuelta al cliente')
                        self.leerDatos(data.decode())
                        connection.sendall(str.encode("Mensaje recibido: ") + data)
                    else:
                        print('Ya no hay mas datos para leer')
                        break

            except Exception as e:
                print(e)
                connection.close()

        print('Sale del todo')
            #finally:
                # Cerrando conexion


# Se define la función main() que es en realidad la que indica
# el comienzo del programa. Dentro de ella se crea el objeto
# aplicación 'miServidor' basado en la clase 'Servidor':

def main():
    miServidor = Servidor()
    return 0

# Mediante el atributo __name__ podemos saber si el código python
# se está usando como módulo o programa principal.
# Si es como módulo podemos usar sus funciones, pero se ejutará
# el método main

if __name__ == '__main__':
    main()