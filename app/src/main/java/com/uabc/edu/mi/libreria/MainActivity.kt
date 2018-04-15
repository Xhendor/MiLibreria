package com.uabc.edu.mi.libreria

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.URLEncoder


class MainActivity : AppCompatActivity(),View.OnClickListener,AdapterView.OnItemClickListener {

    private val PREFS = "prefs"
    private val PREF_NAME = "name"
    private val QUERY_URL = "http://openlibrary.org/search.json?q="
    var jsonAdapter: JSONAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //llamada del displayWelcome
        displayWelcome()
        //Instancia del JSONAdapter
        jsonAdapter = JSONAdapter(this, layoutInflater)
        //Agregar listener al Boton
        libroBuscar.setOnClickListener(this)
        //Agrega el Adaptador a la lista de nuestro Layout
        mainListview.setAdapter(jsonAdapter)
        //Agregar el listener a lista de nuestro Layout
        mainListview.setOnItemClickListener(this)


    }

    override fun onClick(view: View?) {

        busquedaDeLibros(libroNombre.text.toString())

    }

    override fun onItemClick(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Toast.makeText(this, jsonAdapter?.getItem(position)!!.get("title_suggest").toString(), Toast.LENGTH_SHORT).show()

    }


    fun displayWelcome() {
        //Accede a la llave de guardado del dispostivo
        var sharedPreferences = getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        //Lee nombre del usuario
        //o un dato vaicio si no lo encuentra
        val name = sharedPreferences.getString(PREF_NAME, "")

        if (name.length > 0) {

            //Si el nombre es valido mustra el Toast de bienvenida entonces.
            Toast.makeText(this, "Bienvenido de vuelta, $name!", Toast.LENGTH_LONG).show()
        } else {

            // De otra manare, muestra un dialogo que pregunta el nombre
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Hello!")
            alert.setMessage("Cual es tu nombre?")

            // Crea la entrada al EditText
            val input = EditText(this)
            alert.setView(input)

            // Crea un boton de aceptacion para guardar el nombre
            alert.setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, whichButton ->
                        // Toma la entrada del EditText
                        val inputName = input.text.toString()

                        // Pone el valor en memoria y no olviden el Apply
                        val e = sharedPreferences.edit()
                        e.putString(PREF_NAME, inputName)
                        e.apply()
                        e.commit()

                        // Bienvenida al nuevo usuario
                        Toast.makeText(applicationContext, "Bienvenido, $inputName!",
                                Toast.LENGTH_LONG).show()
                    })

            // Crea el boton cancelar
            // Que simplemente cancela o remueve la alerta
            alert.setNegativeButton("Cancelar",
                    DialogInterface.OnClickListener { dialog, whichButton -> })

            //Mostrar la alerta
            alert.show()
        }





    }

    private fun busquedaDeLibros(searchString: String) {

        // Prepare your search string to be put in a URL
        // It might have reserved characters or something
        var urlString = ""
        try {
            urlString = URLEncoder.encode(searchString, "UTF-8")
        } catch (e: UnsupportedEncodingException) {

            // if this fails for some reason, let the user know why
            e.printStackTrace()
            Toast.makeText(this, "Error: " + e.message, Toast.LENGTH_LONG).show()
        }

        // Create a client to perform networking
        val client = AsyncHttpClient()


        // Have the client get a JSONArray of data
        // and define how to respond
        client.get(QUERY_URL + urlString, object : JsonHttpResponseHandler() {


            override fun onSuccess(statusCode:Int, headers:Array<out Header>?, response:JSONObject){
                println(response.get("docs").toString())
                jsonAdapter!!.updateData(response.get("docs") as JSONArray)
            }


            override fun onFailure(statusCode: Int, headers: Array<out Header>?, throwable: Throwable?, errorResponse: JSONArray?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                // Display a "Toast" message
                // to announce the failure
                Toast.makeText(applicationContext, "Error: " + statusCode + " "
                        , Toast.LENGTH_LONG).show()

            }
        })
    }






}
