package app;

public class App {

    public static void main(String[] args) throws Exception {
        System.out.println("Java Chess!");
        Chess c = new Chess(true);
        c.showBoard();

        c.move("e2", "e4", "white"); // Move white pawn on e2 to e4
        c.move("b8", "Nc6", "black"); // Move black Knight on b8 to c6
        // c.move("c6", "Nd4", "black");
        c.move("d7", "d5", "black");
        c.move("e4", "exd5", "white"); // white pawn captures piece on d5
        c.move("d5", "dxc6", "white");
        c.move("b7", "bxc6", "black");
        // Nxe1

    }
}
