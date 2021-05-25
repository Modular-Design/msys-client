package msys.client.optimization;

public interface IPlotCallback {
    void callback_change(int table, int data, int key);
    void callback_addData(int table, int data);
    void callback_clear(int table);
    void callback_newKey(int table, String key);
}
