from tkinter import *
from tkinter import ttk
from tkinter import messagebox
import socket
import sys
import threading
from Database import Database

HOST = "192.168.0.156"
PORT = 8888

class Servidor():
    def __init__(self):
        self.ventana = Tk()
        self.ventana.title('Servidor')
        self.ventana.configure(bg='black')
        self.centrarVentana(self.ventana, 600,600)
        self.bottom = Frame(self.ventana, bd=0, bg='gray')
        self.bottom.pack(side=RIGHT, fill='y')
        self.annadir = Button(self.bottom, text='Añadir Estancia', command=self.agregarEstancia, fg='white',bg='black')
        self.annadir.pack(fill='x')
        self.cambiarNombre = Button(self.bottom, text='Cambiar nombre', command=self.cambiarNombreEstancia, fg='white',
                                    bg='black')
        self.cambiarNombre.pack(fill='x')
        self.eliminar = Button(self.bottom, text='Eliminar Estancia', command=self.eliminarEstancia, fg='white',
                                    bg='black')
        self.eliminar.pack(fill='x')
        self.notebook = ttk.Notebook(self.ventana)
        self.notebook.pack(fill='both', expand='yes')

        servidor = threading.Thread(target=self.iniciarServidor)
        servidor.start()

        db = Database()
        for i in range(db.numeroEstancias()):
            estancia = db.recuperarEstancia(i)
            contenedor = Frame(self.notebook)
            self.notebook.add(child=contenedor, text=estancia[1])
            self.framesPestannas.append(contenedor)
            self.estancias[estancia[1]] = []
            for j in range(db.numeroElementos(estancia[1])):
                elemento = db.recuperarElemento(estancia[1],j)
                label = Label(contenedor, text=elemento[1])
                label.pack()
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

    def agregarEstancia(self):
        self.ventanaSecundaria = Toplevel(self.ventana)
        width = self.ventana.winfo_width() + (self.ventana.winfo_screenwidth() / 2) - (self.ventana.winfo_width() / 2)
        height = (self.ventana.winfo_screenheight() / 2) - (self.ventana.winfo_height() / 2)
        self.ventanaSecundaria.geometry('200x250+%d+%d' % (width, height))
        self.ventanaSecundaria.title('Nueva estancia')
        nombre = Label(self.ventanaSecundaria, text='Nombre', width=30)
        nombre.pack()
        self.recogerNombre = Entry(self.ventanaSecundaria, width=30)
        self.recogerNombre.pack()
        Button(self.ventanaSecundaria, text='Agregar estancia', command=self.agregarPestanna).pack()
        self.ventanaSecundaria.grab_set() # No permite interactuar con otra ventana que no sea ésta
        self.ventana.wait_window(self.ventanaSecundaria)

    def agregarPestanna(self):
        nombre = self.recogerNombre.get()
        estancia = Frame(self.notebook)
        self.framesPestannas.append(estancia)
        opciones = Frame(estancia)
        opciones.pack(side=BOTTOM, fill='x')
        Button(estancia, text = 'Añadir bombilla', command = self.agregarIluminacion, fg = 'white', bg = 'black').pack(side=BOTTOM)
        if (nombre == ''):
            self.notebook.add(estancia, text="Estancia")
        else:
            self.notebook.add(estancia, text=nombre)
        self.recogerNombre.delete(0,'end') # Limpia el cajón
        self.ventanaSecundaria.destroy()

    def cambiarNombreEstancia(self):
        if(len(self.notebook.tabs())>0):
            self.ventanaSecundaria = Toplevel(self.ventana)
            width = self.ventana.winfo_width() + (self.ventana.winfo_screenwidth() / 2) - (self.ventana.winfo_width() / 2)
            height = (self.ventana.winfo_screenheight() / 2) - (self.ventana.winfo_height() / 2)
            self.ventanaSecundaria.geometry('200x250+%d+%d' % (width, height))
            self.ventanaSecundaria.title('Cambiar nombre estancia')
            nombre = Label(self.ventanaSecundaria, text='Nuevo nombre', width=30)
            nombre.pack()
            self.recogerNombre = Entry(self.ventanaSecundaria, width=30)
            self.recogerNombre.pack()
            Button(self.ventanaSecundaria, text='Cambiar nombre', command=self.cambiarNombrePestanna).pack()
            self.ventana.wait_window(self.ventanaSecundaria)
        else:
            messagebox.showerror('Error','No existe ninguna estancia para modificar su nombre')

    def cambiarNombrePestanna(self):
        nombre = self.recogerNombre.get()
        if (nombre != ''):
            self.notebook.tab('current', text=nombre)
        self.recogerNombre.delete(0,'end') # Limpia el cajón

    def eliminarEstancia(self):
        if(len(self.notebook.tabs())>0):
            nombre = self.notebook.tab('current','text')
            if messagebox.askokcancel(title='Comprobación',message='¿Estás seguro de que deseas eliminar la estancia %s?' % (nombre)):
                print(self.framesPestannas[self.notebook.index('current')])
                self.framesPestannas.remove(self.framesPestannas[(self.notebook.index('current'))])
                self.notebook.forget('current')
        else:
            messagebox.showerror('Error','No existe ninguna estancia para eliminar')

    def agregarIluminacion(self):
        self.ventanaSecundaria = Toplevel(self.ventana)
        width = self.ventana.winfo_width() + (self.ventana.winfo_screenwidth() / 2) - (
        self.ventana.winfo_width() / 2)
        height = (self.ventana.winfo_screenheight() / 2) - (self.ventana.winfo_height() / 2)
        self.ventanaSecundaria.geometry('200x250+%d+%d' % (width, height))
        self.ventanaSecundaria.title('Nueva bombilla')
        nombre = Label(self.ventanaSecundaria, text='Nombre', width=30)
        nombre.pack()
        self.recogerNombre = Entry(self.ventanaSecundaria, width=30)
        self.recogerNombre.pack()
        Button(self.ventanaSecundaria, text='Agregar bombilla', command=lambda: self.agregarBombilla(self.framesPestannas[self.notebook.index('current')])).pack()
        self.ventanaSecundaria.grab_set()  # No permite interactuar con otra ventana que no sea ésta
        self.ventana.wait_window(self.ventanaSecundaria)
        
    def agregarBombilla(self, ventana):
        nombre = self.recogerNombre.get()
        if (nombre == ''):
            nombre = 'Bombilla'
        self.label = Label(ventana, text='BOMBILLA')
        self.label.pack()
        #Label(ventana, text="Bombilla 1").place(x=40, y=5)
        #self.estado1 = Label(ventana, text="Apagada")
        #self.estado1.place(x=45, y=140)
        self.recogerNombre.delete(0,'end') # Limpia el cajón
        self.ventanaSecundaria.destroy()

    def leerDatos(self, datos):
        db = Database()
        if datos[0] == '+':
            if datos[1] == 'E':
                contenedor = Frame(self.notebook)
                self.notebook.add(child=contenedor,text=datos[3:])
                self.framesPestannas.append(contenedor)
                self.estancias[datos[3:]]=[]
                db.insertarEstancia(datos[2:3], datos[3:])
            else:
                lista = self.estancias[self.notebook.tab(int(datos[2:3]),'text')]
                #imagen = PhotoImage(file="imagenes\luzapagada2.png")
                label = Label(self.framesPestannas[int(datos[2:3])], text=datos[3:])
                label.pack()
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
                        lista = self.estancias[self.notebook.tab(int(datos[1]), 'text')]
        db.cerrarBD()


    def iniciarServidor(self):
        self.framesPestannas = []
        self.estancias = {}
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
            finally:
                # Cerrando conexion
                connection.close()

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