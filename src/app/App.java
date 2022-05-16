package app;

public class App {

    public static void main(String[] args) throws Exception {
        System.out.println("Java Chess!");
        Chess c = new Chess(true);
        c.showBoard();

        // test some moves out
        c.move("e2", "e4", "white"); // Move white pawn on e2 to e4
        c.move("b8", "Nc6", "black"); // Move black Knight on b8 to c6
        // c.move("c6", "Nd4", "black");
        // c.move("d7", "d5", "black");
        // c.move("e4", "exd5", "white"); // white pawn captures piece on d5
        // c.move("d5", "dxc6", "white");
        // c.move("b7", "bxc6", "black");
        c.move("f1", "Bb5", "white");
        c.move("e7", "e5", "black");
        c.move("g1", "Nf3", "white");
        c.move("d8", "Qh4", "black");
        // Castle (Kingside) todo: O-O and O-O-O in move log
        // c.move("e1", "Kg1", "white");
        // c.move("h1", "Rf1", "white");

        // TODO: Use a while loop and poll for moves to call move() on user input
        // move(from, to)

    }
}
