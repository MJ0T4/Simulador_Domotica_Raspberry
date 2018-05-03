from tkinter import *
from tkinter import ttk
from tkinter import messagebox

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
        self.estancia = Toplevel(self.ventana)
        width = self.ventana.winfo_width() + (self.ventana.winfo_screenwidth() / 2) - (self.ventana.winfo_width() / 2)
        height = (self.ventana.winfo_screenheight() / 2) - (self.ventana.winfo_height() / 2)
        self.estancia.geometry('200x250+%d+%d' % (width, height))
        self.estancia.title('Nueva estancia')
        nombre = Label(self.estancia, text='Nombre', width=30)
        nombre.pack()
        self.recogerNombre = Entry(self.estancia, width=30)
        self.recogerNombre.pack()
        Button(self.estancia, text='Agregar estancia', command=self.agregarPestanna).pack()
        self.estancia.grab_set() # No permite interactuar con otra ventana que no sea ésta
        self.ventana.wait_window(self.estancia)

    def agregarPestanna(self):
        nombre = self.recogerNombre.get()
        estancia = Frame(self.notebook)
        if (nombre == ''):
            self.notebook.add(estancia, text="Estancia")
        else:
            self.notebook.add(estancia, text=nombre)
        self.recogerNombre.delete(0,'end') # Limpia el cajón
        self.estancia.destroy()

    def cambiarNombreEstancia(self):
        if(len(self.notebook.tabs())>0):
            self.estancia = Toplevel(self.ventana)
            width = self.ventana.winfo_width() + (self.ventana.winfo_screenwidth() / 2) - (self.ventana.winfo_width() / 2)
            height = (self.ventana.winfo_screenheight() / 2) - (self.ventana.winfo_height() / 2)
            self.estancia.geometry('200x250+%d+%d' % (width, height))
            self.estancia.title('Cambiar nombre estancia')
            nombre = Label(self.estancia, text='Nuevo nombre', width=30)
            nombre.pack()
            self.recogerNombre = Entry(self.estancia, width=30)
            self.recogerNombre.pack()
            Button(self.estancia, text='Cambiar nombre', command=self.cambiarNombrePestanna).pack()
            self.ventana.wait_window(self.estancia)
        else:
            messagebox.showerror('Error','No existe ninguna estancia para modificar su nombre')

    def cambiarNombrePestanna(self):
        nombre = self.recogerNombre.get()
        estancia = Frame(self.notebook)
        if (nombre != ''):
            self.notebook.tab('current', text=nombre)
        self.recogerNombre.delete(0,'end') # Limpia el cajón

    def eliminarEstancia(self):
        if(len(self.notebook.tabs())>0):
            nombre = self.notebook.tab('current','text')
            if messagebox.askokcancel(title='Comprobación',message='¿Estás seguro de que deseas eliminar la estancia %s?' % (nombre)):
                self.notebook.forget('current')
        else:
            messagebox.showerror('Error','No existe ninguna estancia para eliminar')

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