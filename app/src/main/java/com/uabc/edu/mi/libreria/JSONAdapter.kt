package com.uabc.edu.mi.libreria

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject
import com.squareup.picasso.Picasso

class JSONAdapter internal constructor(private val context: Context,
                                       private val inflater: LayoutInflater) : BaseAdapter() {


    private var jsonArray: JSONArray? = null

    init {
        jsonArray = JSONArray()
    }

    public fun updateData(jsonArray: JSONArray) {

        // Actualiza el adaptador y los datos
        this.jsonArray = jsonArray
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): JSONObject? {
       return jsonArray?.get(position) as JSONObject?
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
   }

    override fun getCount(): Int {
        return jsonArray!!.length()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var convertView = convertView

        val holder: ViewHolder

        // Verifica si la vista existe
        // Si si existe, nos es necesario inflar la vista de nuevo
        if (convertView == null) {

            // Infla nuestro row_book layout
            convertView = inflater.inflate(R.layout.row_book, null)

            // Creamos el nuevo "Holder" con las subvistas
            holder = ViewHolder()
            //Obtenemos por ID
            holder.thumbnailImageView = convertView!!
                    .findViewById(R.id.img_thumbnail) as ImageView
            holder.titleTextView = convertView!!
                    .findViewById(R.id.text_title)
            holder.authorTextView = convertView!!
                    .findViewById(R.id.text_author)

            // Se agrega el holder a la vista para reciclaje
            convertView!!.setTag(holder)

        }else{

            // Como ya esta creada solo se obtiene el ViewHolder de atributo "tag"
            holder = convertView!!.tag as ViewHolder


        }
        // Obtener el libro del arreglo de JSONs
        val jsonObject:JSONObject= getItem(position) as JSONObject

        // Busca si existe una portada en el objeto JSON
        if (jsonObject.has("cover_i")) {
            //Si existe se obtiene el texto del ID de la Imagen
            val imageID = jsonObject.optString("cover_i")

            //Con la ID obtenida contruimos la URL que llamara el API de Imagenes
            val imageURL = (IMAGE_URL_BASE
                    + imageID
                    + "-S.jpg")


            // Usaremos Picasso una libreria que nos ayudara a cargar imagenes
            // temporalmente en caso de que sea lenta la respesta
            Picasso.get()
                    .load(imageURL)
                    .placeholder(R.drawable.ic_books)
                    .into(holder.thumbnailImageView)

        }else {

            // If there is no cover ID in the object, use a placeholder
            holder.thumbnailImageView!!
                    .setImageResource(R.drawable.ic_books)
        }
        // Ahora tomaremos el titulo del libro y el autor del JSON
        var bookTitle = ""
        var authorName = ""

        //Buscamos si existe el titulo
        if (jsonObject.has("title")) {
            bookTitle = jsonObject.optString("title")
        }
        //Buscamos si existe el autor
        if (jsonObject.has("author_name")) {
            authorName = jsonObject.optJSONArray("author_name")
                    .optString(0)
        }

        // Enviamos estos Strings a los TextViews de nuestro row_books layout
        holder.titleTextView!!.text = bookTitle
        holder.authorTextView!!.text = authorName


        //Finalizamos regresando la vista modificada del renglon
        return convertView


    }




    // Esto es utilizado solo cada vez que se infla y busca por ID cada View
    private class ViewHolder {
        internal var thumbnailImageView: ImageView? = null
        internal var titleTextView: TextView? = null
        internal var authorTextView: TextView? = null
    }

    companion object {

        private val IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/"
    }


}


