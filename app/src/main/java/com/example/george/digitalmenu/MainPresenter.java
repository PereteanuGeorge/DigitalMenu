package com.example.george.digitalmenu;

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;
    private final Network network;
    private final QrCodeScanner scanner;

    public MainPresenter(Network network, QrCodeScanner scanner) {
        this.network = network;
        this.scanner = scanner;
    }

    @Override
    public void onViewCompleteCreate() {
        view.checkNetworkConnectivity();
        view.turnScannerOn();
    }

    @Override
    public void registerView(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void submitScanResult(String s) {
        view.switchToMenuActivity(s);
    }
}
