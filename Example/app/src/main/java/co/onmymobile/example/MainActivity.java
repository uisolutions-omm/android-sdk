package co.onmymobile.example;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import co.onmymobile.wifimodule.BranchActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);
        if(isFirstRun)
        {
            BranchActivity.branchInitialization(getApplicationContext());
        }
        else
        {
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);

        }
    }
}
