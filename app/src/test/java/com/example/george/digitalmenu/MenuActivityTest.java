package com.example.george.digitalmenu;

import android.content.Intent;

import com.example.george.digitalmenu.menu.MenuActivity;
import com.example.george.digitalmenu.menu.MenuContract;
import com.example.george.digitalmenu.utils.Dish;
import com.example.george.digitalmenu.utils.Restaurant;
import com.example.george.digitalmenu.utils.ServiceRegistry;
import com.example.george.digitalmenu.utils.TestApp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import static com.example.george.digitalmenu.main.MainActivity.INTENT_KEY;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestApp.class)
public class MenuActivityTest {

    ActivityController<MenuActivity> activityController;
    ServiceRegistry registry;
    MenuContract.Presenter mockPresenter;
    private final String testIntentInput = "bestmangal";

    private Dish testDish = new Dish(
            "testName",
            "testPicUrl",
            "testDescription",
            20.0,
            Arrays.asList("catOne","catTwo"),
            Arrays.asList("GLUTEN_FREE"),
            Arrays.asList("optionOne", "optionTwo")
    );

    private final Restaurant testRestaurant = new Restaurant(
            "testAddress",
            Arrays.asList("catOne", "catTwo"),
            "testTelephone",
            "testWebsite",
            Arrays.asList(testDish),
            "testPicUrl",
            5,
            "Name"
    );

    @Before
    public void setUp() {
        mockPresenter = mock(MenuContract.Presenter.class);

        registry = ServiceRegistry.getInstance();
        registry.registerService(MenuContract.Presenter.class, mockPresenter);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra(INTENT_KEY, testIntentInput);
        activityController = Robolectric.buildActivity(MenuActivity.class, intent);
    }

    @Test
    public void callsOnViewCompleteCreateDuringOnCreateLifeCycle() {
        activityController.create();

        verify(mockPresenter, times(1)).onViewCompleteCreate();
    }

    @Test
    public void registersWithPresenterDuringOnCreateLifeCycle() {
        activityController.create();

        verify(mockPresenter, times(1)).registerView(activityController.get());
    }

    @Test
    public void registersWithPresenterBeforeCallingOnViewCompleteCreate() {
        activityController.create();

        InOrder order = inOrder(mockPresenter);
        order.verify(mockPresenter).registerView(activityController.get());
        order.verify(mockPresenter).onViewCompleteCreate();
    }

    @Test
    public void getRestaurantNameReturnsIntentString() {
        assert activityController.create().get().getRestaurantName().equals(testIntentInput);
    }

//    @Test
//    public void displayMenuAsksForThemePictureFromPresenter() {
//        activityController.create().get().displayMenu(testRestaurant);
//        verify(mockPresenter).fetchThemeImage(eq(testRestaurant), any());
//    }

//    @Test
//    public void displayMenuSetsThemePictureBitmapInCorrectImageView() {
//
//        // Given
//        byte[] testByteArray = new byte[5];
//        Bitmap testBitmap = BitmapFactory.decodeByteArray(testByteArray, 0, testByteArray.length);
//
//        Mockito.doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) throws Throwable {
//                Consumer<Bitmap> successCallback = invocation.getArgument(1);
//                successCallback.accept(testBitmap);
//                return null;
//            }
//        }).when(mockPresenter).fetchThemeImage(eq(testRestaurant), any());
//
//        MenuActivity activity = activityController.create().get();
//
//        // When
//        activity.displayMenu(testRestaurant);
//
//        // Then
//        ImageView themePictureView = activity.findViewById(R.id.theme_picture);
//        Bitmap attachedBitmap = ((BitmapDrawable) themePictureView.getDrawable()).getBitmap();
//        assert attachedBitmap.equals(testBitmap);
//    }


}