    package com.example.myapplication3;

    import android.os.Bundle;
    import android.view.View;
    import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.Spinner;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;

    import com.android.volley.Request;
    import com.android.volley.RequestQueue;
    import com.android.volley.Response;
    import com.android.volley.VolleyError;
    import com.android.volley.toolbox.JsonObjectRequest;
    import com.android.volley.toolbox.Volley;

    import org.json.JSONException;
    import org.json.JSONObject;

    public class MainActivity extends AppCompatActivity {

        private EditText amountEditText;
        private Spinner sourceCurrencySpinner, targetCurrencySpinner;
        private TextView resultTextView;

        private ArrayAdapter<String> spinnerAdapter;
        private String[] currencies = {"USD", "EUR","SGD", "BRL","GBP", "JPY" ,"CNY"};

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            amountEditText = findViewById(R.id.amountEditText);
            sourceCurrencySpinner = findViewById(R.id.sourceCurrencySpinner);
            targetCurrencySpinner = findViewById(R.id.targetCurrencySpinner);
            resultTextView = findViewById(R.id.resultTextView);

            spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sourceCurrencySpinner.setAdapter(spinnerAdapter);
            targetCurrencySpinner.setAdapter(spinnerAdapter);

            Button convertButton = findViewById(R.id.convertButton);
            convertButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String amountStr = amountEditText.getText().toString();
                    String sourceCurrency = sourceCurrencySpinner.getSelectedItem().toString();
                    String targetCurrency = targetCurrencySpinner.getSelectedItem().toString();

                    if (amountStr.isEmpty()) {
                        amountEditText.setError("Không được để trống");
                        return;
                    }
                    double amount = Double.parseDouble(amountStr);

                    // Assuming the API returns a direct conversion rate:
                    String url = String.format("https://economia.awesomeapi.com.br/json/last/%s-%s", sourceCurrency, targetCurrency);
                    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONObject currencyObject = response.getJSONObject(sourceCurrency + targetCurrency);
                                        String currencyName = currencyObject.getString("codein");
                                        double rate = currencyObject.getDouble("ask");
                                        double result = rate * amount;
                                        resultTextView.setText(String.format("%.2f %s", result, currencyName));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(MainActivity.this, "Parsing error!\n" + response.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this, "Lỗi truy cập API!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    queue.add(jsonObjectRequest);
                }
            });
        }
    }