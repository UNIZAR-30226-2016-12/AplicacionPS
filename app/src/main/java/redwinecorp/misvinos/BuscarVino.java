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
    private EditText valorBusqueda;
    private VinosDbAdapter mDbHelper;

    private String parametro;
    private String valor;

    @Override
    /**
     * *     metodo constructor de la clase
     **/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new VinosDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.activity_buscar_vino);

        valorBusqueda = (EditText) findViewById(R.id.nomBusqueda);
        desplegable = (Spinner) findViewById(R.id.spinner);

        Button confirmButton = (Button) findViewById(R.id.confirmar);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (comprobarEntrada()) {
                    //parametro = desplegable.
                    valor = valorBusqueda.getText().toString();
                    buscar();
                }
            }
        });

        String[] arraySpinner = new String[] {
                mDbHelper.KEY_VINO_NOMBRE,mDbHelper.KEY_VINO_AÑO,
                mDbHelper.KEY_VINO_VALORACION,mDbHelper.KEY_VINO_POSICION,
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        desplegable.setAdapter(adapter);
    }
    /**
     * *     metodo que se encarga de comprobar que el formato de los datos que ha introducido el usuario es correctos
     **/
    private boolean comprobarEntrada(){
        switch(parametro){
            case VinosDbAdapter.KEY_VINO_NOMBRE:
                //No necesita comprobación
                return true;
            case VinosDbAdapter.KEY_VINO_AÑO:
                try{
                    int año = Integer.parseInt(valorBusqueda.getText().toString());
                    if(año<0){
                        valorBusqueda.setText("");
                        valorBusqueda.setHintTextColor(Color.rgb(255, 0, 0));
                        valorBusqueda.setHint("El año tiene que ser mayor o igual que 0.");
                        return false;
                    }
                    return true;
                } catch (NumberFormatException e) {
                    valorBusqueda.setText("");
                    valorBusqueda.setHintTextColor(Color.rgb(255, 0, 0));
                    valorBusqueda.setHint("El año tiene que ser un entero.");
                    return false;
                }
            case VinosDbAdapter.KEY_VINO_VALORACION:
                try{
                    int val = Integer.parseInt(valorBusqueda.getText().toString());
                    if(val<0 || val>10){
                        valorBusqueda.setText("");
                        valorBusqueda.setHintTextColor(Color.rgb(255, 0, 0));
                        valorBusqueda.setHint("La valoración tiene que estar en el rango [0-10]");
                        return false;
                    }
                    return true;
                } catch (NumberFormatException e) {
                    valorBusqueda.setText("");
                    valorBusqueda.setHintTextColor(Color.rgb(255, 0, 0));
                    valorBusqueda.setHint("La valoración tiene que ser un entero.");
                    return false;
                }
            case VinosDbAdapter.KEY_VINO_POSICION:
                try{
                    int pos = Integer.parseInt(valorBusqueda.getText().toString());
                    if(pos<0){
                        valorBusqueda.setText("");
                        valorBusqueda.setHintTextColor(Color.rgb(255, 0, 0));
                        valorBusqueda.setHint("La posición tiene que ser mayor o igual que 0.");
                        return false;
                    }
                    return true;
                } catch (NumberFormatException e) {
                    valorBusqueda.setText("");
                    valorBusqueda.setHintTextColor(Color.rgb(255, 0, 0));
                    valorBusqueda.setHint("La posición tiene que ser un entero.");
                    return false;
                }
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
                i.putExtra(MisVinos.MOSTRAR_BUSQUEDA_V,valorBusqueda.getText().toString());
            case VinosDbAdapter.KEY_VINO_AÑO:
                try{
                    int año = Integer.parseInt(valorBusqueda.getText().toString());
                    i.putExtra(MisVinos.MOSTRAR_BUSQUEDA_P,VinosDbAdapter.KEY_VINO_AÑO);
                    i.putExtra(MisVinos.MOSTRAR_BUSQUEDA_V,año);
                } catch (NumberFormatException e) {}
            case VinosDbAdapter.KEY_VINO_VALORACION:
                try{
                    int val = Integer.parseInt(valorBusqueda.getText().toString());
                    i.putExtra(MisVinos.MOSTRAR_BUSQUEDA_P,VinosDbAdapter.KEY_VINO_VALORACION);
                    i.putExtra(MisVinos.MOSTRAR_BUSQUEDA_V,val);
                } catch (NumberFormatException e) {}
            case VinosDbAdapter.KEY_VINO_POSICION:
                try{
                    int pos = Integer.parseInt(valorBusqueda.getText().toString());
                    i.putExtra(MisVinos.MOSTRAR_BUSQUEDA_P,VinosDbAdapter.KEY_VINO_POSICION);
                    i.putExtra(MisVinos.MOSTRAR_BUSQUEDA_V,pos);
                } catch (NumberFormatException e) {}
        }
        startActivity(i);
    }
}
