import controller.Controller;
import model.Data;
import view.GUI;

public class Lab1 {
    public static void main(String[] args) {
        Data model = new Data();
        GUI view = new GUI();
        new Controller(view, model);
    }
}