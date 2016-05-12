package redwinecorp.misvinos;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class BuscarVino extends AppCompatActivity {

    private Spinner desplegable;
    private EditText valorBusqueda1;
    private EditText valorBusqueda2;

    private String parametro;

    @Override
    /**
     * *     metodo constructor de la clase
     **/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_buscar_vino);

        valorBusqueda1 = (EditText) findViewById(R.id.valorBusqueda1);
        valorBusqueda2 = (EditText) findViewById(R.id.valorBusqueda2);
        desplegable = (Spinner) findViewById(R.id.spinner);

        Button confirmButton = (Button) findViewById(R.id.search);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                parametro = (String) desplegable.getItemAtPosition(desplegable.getSelectedItemPosition());
                if (comprobarEntrada()) {
                    buscar();
                }
            }
        });

        String[] arraySpinner = new String[] {
                VinosDbAdapter.KEY_VINO_NOMBRE,VinosDbAdapter.KEY_VINO_AÑO,VinosDbAdapter.KEY_VINO_VALORACION,
                VinosDbAdapter.KEY_VINO_POSICION,VinosDbAdapter.KEY_ES_TIPO,VinosDbAdapter.KEY_POSEE_DENOMINACION
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        desplegable.setAdapter(adapter);
    }
    /**
     * *     metodo que se encarga de comprobar que el formato de los datos que ha introducido el usuario es correctos
     **/
    private boolean comprobarEntrada(){
        switch(parametro){
            case VinosDbAdapter.KEY_VINO_NOMBRE:
                if(valorBusqueda1.getText().toString().equals("")){
                    valorBusqueda2.setText("");
                    valorBusqueda2.setHintTextColor(Color.rgb(255, 0, 0));
                    valorBusqueda2.setHint("Para realizar una búsqueda por nombre no deje este campo vacío.");
                    return false;
                }
                else if(!(valorBusqueda2.getText().toString().equals(""))){
                    valorBusqueda2.setText("");
                    valorBusqueda2.setHintTextColor(Color.rgb(255, 0, 0));
                    valorBusqueda2.setHint("Para realizar una búsqueda por nombre deje este campo vacío.");
                    return false;
                }
                return true;
            case VinosDbAdapter.KEY_VINO_AÑO:
                if(valorBusqueda1.getText().toString().equals("")){
                    valorBusqueda1.setText("");
                    valorBusqueda1.setHintTextColor(Color.rgb(255, 0, 0));
                    valorBusqueda1.setHint("Para realizar una búsqueda por año introduzca aquí el mínimo.");
                    return false;
                }
                else if(valorBusqueda2.getText().toString().equals("")){
                    valorBusqueda2.setText("");
                    valorBusqueda2.setHintTextColor(Color.rgb(255, 0, 0));
                    valorBusqueda2.setHint("Para realizar una búsqueda por año introduzca aquí el máximo.");
                    return false;
                }
                else {
                    try {
                        int año1 = Integer.parseInt(valorBusqueda1.getText().toString());
                        int año2 = Integer.parseInt(valorBusqueda2.getText().toString());
                        if(año1<0){
                            valorBusqueda1.setText("");
                            valorBusqueda1.setHintTextColor(Color.rgb(255, 0, 0));
                            valorBusqueda1.setHint("Los años tienen que ser enteros positivos o 0.");
                            return false;
                        }
                        else if(año2<0){
                            valorBusqueda2.setText("");
                            valorBusqueda2.setHintTextColor(Color.rgb(255, 0, 0));
                            valorBusqueda2.setHint("Los años tienen que ser enteros positivos o 0.");
                            return false;
                        }
                        else if (año1 > año2) {
                            valorBusqueda1.setText("");
                            valorBusqueda1.setHintTextColor(Color.rgb(255, 0, 0));
                            valorBusqueda1.setHint("Este campo tiene que ser menor o igual que el otro.");
                            valorBusqueda2.setText("");
                            valorBusqueda2.setHintTextColor(Color.rgb(255, 0, 0));
                            valorBusqueda2.setHint("Este campo tiene que ser mayor o igual que el otro.");
                            return false;
                        }
                        else {
                            return true;
                        }
                    } catch (NumberFormatException e) {
                        valorBusqueda1.setText("");
                        valorBusqueda1.setHintTextColor(Color.rgb(255, 0, 0));
                        valorBusqueda1.setHint("Los años deben ser enteros.");
                        valorBusqueda2.setText("");
                        valorBusqueda2.setHintTextColor(Color.rgb(255, 0, 0));
                        valorBusqueda2.setHint("Los años deben ser enteros.");
                        return false;
                    }
                }
            case VinosDbAdapter.KEY_VINO_VALORACION:
                if(valorBusqueda1.getText().toString().equals("")){
                    valorBusqueda1.setText("");
                    valorBusqueda1.setHintTextColor(Color.rgb(255, 0, 0));
                    valorBusqueda1.setHint("Para realizar una búsqueda por valoración, introduzca aquí la mínima.");
                    return false;
                }
                else if(valorBusqueda2.getText().toString().equals("")){
                    valorBusqueda2.setText("");
                    valorBusqueda2.setHintTextColor(Color.rgb(255, 0, 0));
                    valorBusqueda2.setHint("Para realizar una búsqueda por valoración, introduzca aquí la mínima.");
                    return false;
                }
                else {
                    try {
                        int val1 = Integer.parseInt(valorBusqueda1.getText().toString());
                        int val2 = Integer.parseInt(valorBusqueda2.getText().toString());
                        if (val1 < 0 || val1 > 10) {
                            valorBusqueda1.setText("");
                            valorBusqueda1.setHintTextColor(Color.rgb(255, 0, 0));
                            valorBusqueda1.setHint("Las valoraciones son enteros en el rango [0-10].");
                            return false;
                        } else if (val2 < 0 || val2 > 10) {
                            valorBusqueda2.setText("");
                            valorBusqueda2.setHintTextColor(Color.rgb(255, 0, 0));
                            valorBusqueda2.setHint("Las valoraciones son enteros en el rango [0-10].");
                            return false;
                        } else if (val1 > val2) {
                            valorBusqueda1.setText("");
                            valorBusqueda1.setHintTextColor(Color.rgb(255, 0, 0));
                            valorBusqueda1.setHint("Este campo tiene que ser menor o igual que el otro.");
                            valorBusqueda2.setText("");
                            valorBusqueda2.setHintTextColor(Color.rgb(255, 0, 0));
                            valorBusqueda2.setHint("Este campo tiene que ser mayor o igual que el otro.");
                            return false;
                        } else {
                            return true;
                        }
                    } catch (NumberFormatException e) {
                        valorBusqueda1.setText("");
                        valorBusqueda1.setHintTextColor(Color.rgb(255, 0, 0));
                        valorBusqueda1.setHint("Las valoraciones deben ser enteros(0-10).");
                        valorBusqueda2.setText("");
                        valorBusqueda2.setHintTextColor(Color.rgb(255, 0, 0));
                        valorBusqueda2.setHint("Los valoraciones deben ser enteros(0-10).");
                        return false;
                    }
                }
            case VinosDbAdapter.KEY_VINO_POSICION:
                if(valorBusqueda1.getText().toString().equals("")){
                    valorBusqueda1.setText("");
                    valorBusqueda1.setHintTextColor(Color.rgb(255, 0, 0));
                    valorBusqueda1.setHint("Para realizar una búsqueda por posición introduzca aquí la mínima.");
                    return false;
                }
                else if(valorBusqueda2.getText().toString().equals("")){
                    valorBusqueda2.setText("");
                    valorBusqueda2.setHintTextColor(Color.rgb(255, 0, 0));
                    valorBusqueda2.setHint("Para realizar una búsqueda por posición introduzca aquí la máxima.");
                    return false;
                }
                else {
                    try {
                        int pos1 = Integer.parseInt(valorBusqueda1.getText().toString());
                        int pos2 = Integer.parseInt(valorBusqueda2.getText().toString());
                        if(pos1<0){
                            valorBusqueda1.setText("");
                            valorBusqueda1.setHintTextColor(Color.rgb(255, 0, 0));
                            valorBusqueda1.setHint("Las posiciones tienen que ser enteros positivos o 0.");
                            return false;
                        }
                        else if(pos2<0){
                            valorBusqueda2.setText("");
                            valorBusqueda2.setHintTextColor(Color.rgb(255, 0, 0));
                            valorBusqueda2.setHint("Las posiciones tienen que ser enteros positivos o 0.");
                            return false;
                        }
                        else if (pos1 > pos2) {
                            valorBusqueda1.setText("");
                            valorBusqueda1.setHintTextColor(Color.rgb(255, 0, 0));
                            valorBusqueda1.setHint("Este campo tiene que ser menor o igual que el otro.");
                            valorBusqueda2.setText("");
                            valorBusqueda2.setHintTextColor(Color.rgb(255, 0, 0));
                            valorBusqueda2.setHint("Este campo tiene que ser mayor o igual que el otro.");
                            return false;
                        }
                        else {
                            return true;
                        }
                    } catch (NumberFormatException e) {
                        valorBusqueda1.setText("");
                        valorBusqueda1.setHintTextColor(Color.rgb(255, 0, 0));
                        valorBusqueda1.setHint("Las posiciones deben ser enteros.");
                        valorBusqueda2.setText("");
                        valorBusqueda2.setHintTextColor(Color.rgb(255, 0, 0));
                        valorBusqueda2.setHint("Las posiciones deben ser enteros.");
                        return false;
                    }
                }
            case VinosDbAdapter.KEY_ES_TIPO:
                if(valorBusqueda1.getText().toString().equals("")){
                    valorBusqueda2.setText("");
                    valorBusqueda2.setHintTextColor(Color.rgb(255, 0, 0));
                    valorBusqueda2.setHint("Para realizar una búsqueda por nombre no deje este campo vacío.");
                    return false;
                }
                else if(!valorBusqueda2.getText().toString().equals("")){
                    valorBusqueda2.setText("");
                    valorBusqueda2.setHintTextColor(Color.rgb(255, 0, 0));
                    valorBusqueda2.setHint("Para realizar una búsqueda por tipo deje este campo vacío.");
                    return false;
                }
                return true;
            case VinosDbAdapter.KEY_POSEE_DENOMINACION:
                if(valorBusqueda1.getText().toString().equals("")){
                    valorBusqueda2.setText("");
                    valorBusqueda2.setHintTextColor(Color.rgb(255, 0, 0));
                    valorBusqueda2.setHint("Para realizar una búsqueda por nombre no deje este campo vacío.");
                    return false;
                }
                else if(!valorBusqueda2.getText().toString().equals("")){
                    valorBusqueda2.setText("");
                    valorBusqueda2.setHintTextColor(Color.rgb(255, 0, 0));
                    valorBusqueda2.setHint("Para realizar una búsqueda por denominación deje este campo vacío.");
                    return false;
                }
                return true;
            default:
                return false;
        }
    }

    /**
     * *     metodo que se encarga de realizar la busqueda deseada por el usuario
     **/
    private void buscar(){
        Intent i = new Intent(this, MisVinos.class);
        switch(parametro){
            case VinosDbAdapter.KEY_VINO_NOMBRE:
                i.putExtra(MisVinos.MOSTRAR_BUSQUEDA_P,VinosDbAdapter.KEY_VINO_NOMBRE);
                i.putExtra(MisVinos.MOSTRAR_BUSQUEDA_V1,valorBusqueda1.getText().toString());
                break;
            case VinosDbAdapter.KEY_VINO_AÑO:
                try{
                    i.putExtra(MisVinos.MOSTRAR_BUSQUEDA_P,VinosDbAdapter.KEY_VINO_AÑO);
                    i.putExtra(MisVinos.MOSTRAR_BUSQUEDA_V1,valorBusqueda1.getText().toString());
                    i.putExtra(MisVinos.MOSTRAR_BUSQUEDA_V2,valorBusqueda2.getText().toString());
                } catch (NumberFormatException e) {}
                break;
            case VinosDbAdapter.KEY_VINO_VALORACION:
                try{
                    i.putExtra(MisVinos.MOSTRAR_BUSQUEDA_P,VinosDbAdapter.KEY_VINO_VALORACION);
                    i.putExtra(MisVinos.MOSTRAR_BUSQUEDA_V1,valorBusqueda1.getText().toString());
                    i.putExtra(MisVinos.MOSTRAR_BUSQUEDA_V2,valorBusqueda2.getText().toString());
                } catch (NumberFormatException e) {}
                break;
            case VinosDbAdapter.KEY_VINO_POSICION:
                try{
                    i.putExtra(MisVinos.MOSTRAR_BUSQUEDA_P,VinosDbAdapter.KEY_VINO_POSICION);
                    i.putExtra(MisVinos.MOSTRAR_BUSQUEDA_V1,valorBusqueda1.getText().toString());
                    i.putExtra(MisVinos.MOSTRAR_BUSQUEDA_V2,valorBusqueda2.getText().toString());
                } catch (NumberFormatException e) {}
                break;
            case VinosDbAdapter.KEY_ES_TIPO:
                i.putExtra(MisVinos.MOSTRAR_BUSQUEDA_P,VinosDbAdapter.KEY_ES_TIPO);
                i.putExtra(MisVinos.MOSTRAR_BUSQUEDA_V1,valorBusqueda1.getText().toString());
                break;
            case VinosDbAdapter.KEY_POSEE_DENOMINACION:
                i.putExtra(MisVinos.MOSTRAR_BUSQUEDA_P,VinosDbAdapter.KEY_POSEE_DENOMINACION);
                i.putExtra(MisVinos.MOSTRAR_BUSQUEDA_V1,valorBusqueda1.getText().toString());
                break;
        }
        startActivity(i);
    }
}
