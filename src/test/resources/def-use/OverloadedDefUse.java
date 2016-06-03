public class OverloadedDefUse {

    public void m() {
        int x = 22 + 2;
        m(x);
    }

    public void m(int a) {
        int y = a*2;
        int z = y + 1;
    }
}