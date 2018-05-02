from tkinter import *

class Servidor():
    def __init__(self):
        self.ventana = Tk()
        self.ventana.mainloop()

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