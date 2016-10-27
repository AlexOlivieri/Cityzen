package ch.hevs.datasemlab.cityzen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

import ch.hevs.datasemlab.cityzen.timetravel.ItineraryActivity;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        ImageView imageView = (ImageView) findViewById(R.id.logo);
        imageView.setImageResource(R.drawable.logo2);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        imageView.startAnimation(myFadeInAnimation);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

                View detailsView = layoutInflater.inflate(R.layout.details_popup_layout, null);
//        final PopupWindow popupWindow = new PopupWindow(detailsView, LayoutParams.WRAP_CONTENT , LayoutParams.WRAP_CONTENT);
                final PopupWindow popupWindow = new PopupWindow(detailsView, 800 , 600);
//        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));


                Button dismissButton = (Button) detailsView.findViewById(R.id.dismiss_button);
                dismissButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });

//        popupWindow.showAsDropDown(view);
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            }
        });
    }

    public void exploreCulturalInterests(View view){

        Intent intent = new Intent(this, TemporalActivity.class);
        startActivity(intent);
    }

    public void goToItinerary(View view){
        Intent intent = new Intent(this, ItineraryActivity.class);
        startActivity(intent);
    }

    public void showDetails(View view){
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        View detailsView = layoutInflater.inflate(R.layout.details_popup_layout, null);
//        final PopupWindow popupWindow = new PopupWindow(detailsView, LayoutParams.WRAP_CONTENT , LayoutParams.WRAP_CONTENT);
        final PopupWindow popupWindow = new PopupWindow(detailsView, 1200 , 800);
//        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));


        Button dismissButton = (Button) detailsView.findViewById(R.id.dismiss_button);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

//        popupWindow.showAsDropDown(view);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

    }
}
