package redwinecorp.misvinos;

import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Inicio extends AppCompatActivity {

    // Para diferenciar si hay que mostrar Vinos o Grupos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inicio);

        Button BVerVinos = (Button) findViewById(R.id.seeWines);
        Button BVerGrupos = (Button) findViewById(R.id.seeGroups);
        Button BBuscarVino = (Button) findViewById(R.id.search);

        //Si se pulsa el botón VerVinos
        BVerVinos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mostrarVinos();
            }
        });

        //Si se pulsa el botón VerGrupos
        BVerGrupos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //
            }
        });

        //Si se pulsa el botón Buscar
        BBuscarVino.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                buscarVino();
            }
        });
    }

    /**
     * Inicia una nueva actividad MisVinos para mostrar todos los vinos
     */
    private void mostrarVinos() {
        Intent i = new Intent(this, MisVinos.class);
        i.putExtra(MisVinos.MOSTRAR_VINOS, new Boolean(true));
        startActivity(i);
    }

    /**
     * Inicia una nueva actividad Buscar para realizar la búsqueda
     */
    private void buscarVino() {
        Intent i = new Intent(this, BuscarVino.class);
        startActivity(i);
    }
}
