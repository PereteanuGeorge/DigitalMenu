package com.example.george.digitalmenu;

import android.graphics.Bitmap;
import android.support.v4.util.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MenuPresenterTest {

    RestaurantDatabase mockModel;
    MenuContract.View mockView;
    MenuContract.Presenter presenter;
    private static final String testRestaurantName = "bestmangal";
    private Dish testDish = new Dish(
            "testName",
            "testUrl",
            "testDescription",
            20.0,
            Arrays.asList(
                    "cat1",
                    "cat2"
            ),
            Arrays.asList(
                    "tag1",
                    "tag2"
            )
    );

    @Before
    public void init() {
        mockModel = mock(RestaurantDatabase.class);
        mockView = mock(MenuContract.View.class);
        presenter = new MenuPresenter(mockView,mockModel);
    }

    @Test
    public void initialisesModelOnViewCreateComplete() {
        presenter.onViewCompleteCreate();
        verify(mockModel).init(any(), any());
    }

    @Test
    public void notifyViewOnModelInitFailure() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Runnable failure = (Runnable) invocation.getArgument(1);
                failure.run();
                return null;
            }
        }).when(mockModel).init(any(), any());

        presenter.onViewCompleteCreate();

        verify(mockView).notifyModelInitFailure();
    }

    @Test
    public void fetchesRestaurantDetailsOnModelInitSuccess() {
        // Given
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Runnable success = (Runnable) invocation.getArgument(0);
                success.run();
                return null;
            }
        }).when(mockModel).init(any(), any());

        when(mockView.getRestaurantName()).thenReturn(testRestaurantName);

        // When
        presenter.onViewCompleteCreate();

        // Then
        verify(mockModel, times(1)).getRestaurant(eq(testRestaurantName), any());
    }

    @Test
    public void fetchPictureDownloadsFromDatabase() {

        Consumer<Bitmap> testCallback = bitmap -> {};

        // When
        presenter.fetchDishImage(testDish, testCallback);

        // Then
        verify(mockModel, times(1)).downloadDishPicture(testDish, testCallback);
    }
}