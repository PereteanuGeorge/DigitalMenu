package com.example.george.digitalmenu;

import com.example.george.digitalmenu.main.MainContract;
import com.example.george.digitalmenu.main.MainPresenter;
import com.example.george.digitalmenu.utils.QrCodeScanner;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MainPresenterTest {

    MainContract.View mockView;
    QrCodeScanner mockScanner;
    MainContract.Presenter presenter;

    @Before
    public void init() {
        mockView = mock(MainContract.View.class);
        mockScanner = mock(QrCodeScanner.class);
        presenter = new MainPresenter(mockView, mockScanner);
    }

    @Test
    public void checksNetworkConnectivityOnCreateCompleted() {

    }

    @Test
    public void switchesScannerOnAfterCreateCompletes() {
        presenter.onViewCompleteCreate();
        verify(mockScanner, times(1)).scan(any());
    }



}
