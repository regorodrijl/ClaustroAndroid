package com.example.joseluis.claustrorecyclerview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class pintando extends Activity {
    RelativeLayout firma;
    DrawingView dv, view;
    boolean sdDisponhible = false;
    boolean sdAccesoEscritura = false;
    private Paint mPaint;
    String nombre;
    int posicion;
    Intent intent, datos_volta;
    byte[] byteArray;
    private String urlServidor = "http://regorodri.noip.me/ProyectoDawClaustro/librerias/php/android.php";
    //private String urlServidor = "http://claustros.iessanclemente.net/librerias/php/android.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pintando);
        intent = getIntent();
        TextView tvResultado = (TextView) findViewById(R.id.tv_resultado);
        nombre = intent.getExtras().getString("nombre");
        posicion = intent.getExtras().getInt("cod");
        Toast.makeText(getApplicationContext(), "profe numero " + posicion, Toast.LENGTH_SHORT).show();

        tvResultado.setText("Profesor/a: " + intent.getExtras().getString("nombre"));
        comprobarEstadoSD();
        dv = new DrawingView(this);

        firma = (RelativeLayout) findViewById(R.id.firma);
        firma.addView(dv);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(3);
    }

    public void finish() {
        super.finish();
        Toast.makeText(this, "Saíches da actividade secundaria ", Toast.LENGTH_SHORT).show();
    }

    public void guardar(View view) {
        // Do something in response to button click
        save(firma);
        Toast.makeText(getApplicationContext(), "Click en Guardar", Toast.LENGTH_SHORT).show();
        // dv.saveDrawing();
    }

    public void comprobarEstadoSD() {
        String estado = Environment.getExternalStorageState();

        Log.d("SD", estado);

        if (estado.equals(Environment.MEDIA_MOUNTED)) {
            sdDisponhible = true;
            sdAccesoEscritura = true;
        } else if (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
            sdDisponhible = true;

    }

    public void volver(View view) {
        // Do something in response to button click
        Toast.makeText(getApplicationContext(), "Click en Volver", Toast.LENGTH_SHORT).show();

        finish();
    }


    public void borrar(View view) {
        // Do something in response to button click
        Toast.makeText(getApplicationContext(), "Click en Borrar", Toast.LENGTH_SHORT).show();

        dv.clearDrawing();
    }

    private void save(View view) {
        try {

            View content = view;
            content.setDrawingCacheEnabled(true);
            content.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            Bitmap bitmap = content.getDrawingCache();


            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byteArray = byteArrayOutputStream.toByteArray();

            //if (sdDisponhible) {
            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/FIRMAS");
            Log.d("RUTA", mFolder.toString());
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }
            String s = nombre + ".png";
            File f = new File(mFolder.getAbsolutePath(), s);

            FileOutputStream fos = null;
            fos = new FileOutputStream(f);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            Toast.makeText(getBaseContext(), "Imagen Guardada", Toast.LENGTH_SHORT).show();
            /*} else {
                Toast.makeText(this, "Sin tarjeta, no se puede guardar archivo", Toast.LENGTH_SHORT).show();
            }*/

            String imgBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.d("img", imgBase64);
            bitmap.recycle();

            InsertData task1 = new InsertData();
            String name1, img;
            name1 = nombre;
            img = imgBase64;
            task1.execute(urlServidor, name1, img);
        } catch (FileNotFoundException e) {
            Log.d("Save", e.getMessage());
            Toast.makeText(getBaseContext(), "Al Guardar " + e.toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.d("Save", e.getMessage());
            Toast.makeText(getBaseContext(), "Error al guardar " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    // Dibujar Clase DRAWINGVIEW
    public class DrawingView extends View {

        public int width = 0;
        public int height = 0;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;

        public DrawingView(Context c) {
            super(c);
            context = c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(1f);
            setDrawingCacheEnabled(true);
        }

        public void clearDrawing() {
            setDrawingCacheEnabled(false);
            mCanvas.drawColor(Color.WHITE);
            //onSizeChanged(width, height, width, height);
            // onSizeChanged(width, height, width, height);
            invalidate();

            setDrawingCacheEnabled(true);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mCanvas.drawColor(0xFFFFFFFF);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
            canvas.drawPath(circlePath, circlePaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }

    private class InsertData extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(pintando.this);

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection conn = null;
            String params = "nombre=" + urls[1] + "&img=" + urls[2];
            byte[] postData = params.getBytes();
            try {
                URL url = new URL(urls[0]);
                conn = (HttpURLConnection) url.openConnection();

                // Activar método POST
                conn.setDoOutput(true);

                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("charset", "utf-8");

                // Tamaño desconocido
                conn.setFixedLengthStreamingMode(Integer.parseInt(Integer.toString(postData.length)));
                conn.setUseCaches(false);

                OutputStream out = conn.getOutputStream();
                // Usas tu método ingeniado para convertir el archivo a bytes
                out.write(postData);
                out.flush();
                out.close();

                //do somehting with response
                int responseCode = conn.getResponseCode();
                Log.d("cod", String.valueOf(responseCode));
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    StringBuilder sb = new StringBuilder();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(
                            conn.getInputStream()));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        sb.append(line);
                    }

                    conn.disconnect();

                    if (sb.toString() == null || sb.toString() == "") {
                        return "Firma Guardada!";
                    } else {
                        return sb.toString();
                    }
                } else {
                    Log.d("err", "error respuesta : " + responseCode);
                    return "E.Respuesta : " + responseCode;
                    //return "false : " + responseCode;
                }
            } catch (IllegalStateException e) {
                return " IllegalStateException: " + e.getMessage();
            } catch (UnsupportedEncodingException e) {
                return " UnsupportedEncodingException: " + e.getMessage();
            } catch (ProtocolException e) {
                return " ProtocolException: " + e.getMessage();
            } catch (MalformedURLException e) {
                return " MalformedURLException: " + e.getMessage();
            } catch (IOException e) {
                return " Exception IO: " + e.getMessage();
            } catch (Exception e) {
                return " Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Por favor espere, guardando firma...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("result", result);

            Toast.makeText(getApplicationContext(), result,
                    Toast.LENGTH_LONG).show();
            dialog.dismiss();

            datos_volta = new Intent();
            //datos_volta.putExtra("codProfe", intent.getExtras().getString("nombre")); //   .getInt("cod"));
            datos_volta.putExtra("codProfe", intent.getExtras().getInt("cod"));
            setResult(RESULT_OK, datos_volta);
            finish();
        }
    }
}

