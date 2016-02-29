package es.unizar.eina.notepadv3;

import android.util.Log;

public class Test {

    private NotesDbAdapter notesDbAdapter;

    public Test(NotesDbAdapter notesDbAdapter){
        this.notesDbAdapter = notesDbAdapter;
    }

    public void realizarPruebas(){

        //Pruebas de creacion
        boolean correcto = pruebasCrear();
        String TAG = "Salida_Pruebas_Crear";
        if(correcto){
            Log.d(TAG, "realizarPruebas: CORRECTO");
        } else{
            Log.d(TAG, "realizarPruebas: ERROR");
        }

        //Pruebas de actualizacion
        boolean correcto2 = pruebasActualizacion();
        TAG = "Salida_Pruebas_Modificar";
        if(correcto2){
            Log.d(TAG, "realizarPruebas: CORRECTO");
        } else{
            Log.d(TAG, "realizarPruebas: ERROR");
        }

        //Pruebas de borrado
        boolean correcto3 = pruebasBorrado();
        TAG = "Salida_Pruebas_Borrar";
        if(correcto3){
            Log.d(TAG, "realizarPruebas: CORRECTO");
        } else{
            Log.d(TAG, "realizarPruebas: ERROR");
        }

        //Pruebas de volumen
        //pruebasVolumen();
        /*TAG = "Salida_Pruebas_Volumen";
        if(correcto4){
            Log.d(TAG, "realizarPruebas: CORRECTO");
        } else{
            Log.d(TAG, "realizarPruebas: ERROR");
        }*/

        pruebasSobrecarga();

    }

    public boolean pruebasCrear(){
        boolean[] resultados = new boolean[4];
        int antes = notesDbAdapter.getIdMaximo();
        try{
            long salida = notesDbAdapter.createNote("Titulo","Cuerpo",null);
            resultados[0] = (salida != -1);
        } catch(Throwable e){
            String TAG = "TEST_Todo_Correcto";
            Log.d(TAG, "pruebasCrear: Excepcion inesperada");
        }

        try{
            long salida = notesDbAdapter.createNote("","Cuerpo",null);
            resultados[1] = (salida == -1);
        } catch(Throwable e){
            String TAG = "TEST_Titulo_Vacia";
            Log.d(TAG, "pruebasCrear: Excepcion inesperada");
        }

        try{
            long salida = notesDbAdapter.createNote(null,"Cuerpo",null);
            resultados[2] = (salida == -1);
        } catch(Throwable e){
            String TAG = "TEST_Titulo_Null";
            Log.d(TAG, "pruebasCrear: Excepcion inesperada");
        }

        try{
            long salida = notesDbAdapter.createNote("Titulo",null,null);
            resultados[3] = (salida == -1);
        } catch(Throwable e){
            String TAG = "TEST_Cuerpo_Null";
            Log.d(TAG, "pruebasCrear: Excepcion inesperada");
        }

        boolean fin = true;

        for (int i = 0; i < resultados.length; i++){
            if(!resultados[i]){
                fin = false;
            }
        }

        int despues = notesDbAdapter.getIdMaximo();

        for(int i = antes+1; i <= despues; i++){
            notesDbAdapter.deleteNote(i);
        }
        return fin;
    }

    public boolean pruebasActualizacion(){
        boolean[] resultados = new boolean[5];
        for(int i = 0; i < 5; i++){
            long rowID = -1;
            try{
                rowID = notesDbAdapter.createNote("Titulo","Cuerpo",null);
            } catch(Throwable e){
                String TAG = "TEST_CrearNota";
                Log.d(TAG, "pruebasCrear: Excepcion inesperada");
            }

            if(rowID != -1){
                switch(i){
                    case 0:
                        try{
                            boolean correcto = notesDbAdapter.updateNote(rowID,"Titulo2","Cuerpo2",null);
                            resultados[0] = correcto;
                        } catch(Throwable e){
                            String TAG = "TEST_Mod_TC_Correcto";
                            Log.d(TAG, "pruebasActualizacion: Excepcion inesperada");
                        }
                        break;
                    case 1:
                        try{
                            boolean correcto = notesDbAdapter.updateNote(rowID,"","Cuerpo2",null);
                            resultados[1] = !correcto;
                        } catch(Throwable e){
                            String TAG = "TEST_Mod_T_Vacio";
                            Log.d(TAG, "pruebasActualizacion: Excepcion inesperada");
                        }
                        break;
                    case 2:
                        try{
                            boolean correcto = notesDbAdapter.updateNote(rowID,null,"Cuerpo2",null);
                            resultados[2] = !correcto;
                        } catch(Throwable e){
                            String TAG = "TEST_Mod_T_Null";
                            Log.d(TAG, "pruebasActualizacion: Excepcion inesperada");
                        }
                        break;
                    case 3:
                        try{
                            boolean correcto = notesDbAdapter.updateNote(rowID,"Titulo2",null,null);
                            resultados[3] = !correcto;
                        } catch(Throwable e){
                            String TAG = "TEST_Mod_C_Null";
                            Log.d(TAG, "pruebasActualizacion: Excepcion inesperada");
                        }
                        break;
                    case 4:
                        int maximo = notesDbAdapter.getIdMaximo();
                        try{
                            boolean correcto = notesDbAdapter.updateNote(maximo+1,"Titulo2","Cuerpo2",null);
                            resultados[4] = !correcto;
                        } catch(Throwable e){
                            String TAG = "TEST_Mod_ID_Unknown";
                            Log.d(TAG, "pruebasActualizacion: Excepcion inesperada");
                        }
                        break;
                    default:
                        break;
                }
            }

            notesDbAdapter.deleteNote(rowID);

        }
        boolean fin = true;

        for (int i = 0; i < resultados.length; i++){
            if(!resultados[i]){
                fin = false;
            }
        }

        return fin;
    }

    public boolean pruebasBorrado(){
        boolean[] resultados = new boolean[2];

        for(int i = 0; i < 2; i++){
            long rowID = -1;
            try{
                rowID = notesDbAdapter.createNote("Titulo","Cuerpo",null);
            } catch(Throwable e){
                String TAG = "TEST_CrearNota";
                Log.d(TAG, "pruebasBorrado: Excepcion inesperada");
            }

            if(rowID != -1){
                if (i == 0){
                    boolean salida = notesDbAdapter.deleteNote(rowID);
                    resultados[0] = salida;
                } else{
                    int maximo = notesDbAdapter.getIdMaximo();
                    boolean salida = notesDbAdapter.deleteNote(maximo+1);
                    resultados[1] = !salida;
                    notesDbAdapter.deleteNote(rowID);
                }
            }
        }

        boolean fin = true;

        for (int i = 0; i < resultados.length; i++){
            if(!resultados[i]){
                fin = false;
            }
        }

        return fin;
    }

    public void pruebasVolumen(){
        int numNotasMax = 1010;
        boolean[] resultados = new boolean[numNotasMax];
        long antes = -1;
        for(int i = 0; i < numNotasMax; i++){
            try{
                long resultado = notesDbAdapter.createNote("Titulo" + i,"",null);
                resultados[i] = (resultado != -1);
                if(i == 0) antes = resultado;
            } catch (Throwable e){
                String TAG = "TEST_Volmen";
                Log.d(TAG, "pruebasVolumen: Excepcion inesperada");
            }
        }
        long despues = antes + numNotasMax;

        for(long i = antes; i <= despues; i++){
            notesDbAdapter.deleteNote(i);
        }
    }

    public void pruebasSobrecarga(){
        int i = 0;
        int numCaracteres = 1;
        String insertar = "a";
        long resultado = notesDbAdapter.createNote("Titulo" + i,insertar,null);
        while(true){
            notesDbAdapter.deleteNote(resultado);
            System.out.println("Creando con cuerpo de longitud:" + numCaracteres);
            i++;
            numCaracteres += 100000;
            insertar = new String(new char[numCaracteres]).replace('\0', 'a');
            try{
                resultado = notesDbAdapter.createNote("Titulo" + i,insertar,null);
            } catch (Throwable e){
                String TAG ="Test_Sobrecarga";
                Log.d(TAG, "pruebasSobrecarga: Ultimo tamanho valido=" + numCaracteres);
            }
        }
    }
}
