public class DefUseTest {

    public void m() {
        int x = 5 + 8 + 7;
        int y = x*3;
        int z = y*x;
        int t = z;
        n(t);
    }

    public int n(int a) {
        return a*3;
    }
}