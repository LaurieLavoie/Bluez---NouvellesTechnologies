package bluez.bluez;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class AnswerActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_answer);
        TextView txtQuestion = (TextView) findViewById(R.id.txtQuestion);
        final Button btnAnswer1 = (Button) findViewById(R.id.btnAnswer1);
        final Button btnAnswer2 = (Button) findViewById(R.id.btnAnswer2);
        final Button btnAnswer3 = (Button) findViewById(R.id.btnAnswer3);
       btnAnswer1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                sendAnswer(v, btnAnswer1.getText().toString());
            }
        });
        btnAnswer2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                sendAnswer(v, btnAnswer2.getText().toString());
            }
        });
        btnAnswer3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                sendAnswer(v, btnAnswer3.getText().toString());
            }
        });


    }

    private void sendAnswer(View v, String answer)
    {
        //Send answer
    }

    private void receiveQuestion()
    {
        //receive question
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_answer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
