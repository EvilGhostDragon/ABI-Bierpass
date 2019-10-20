package `in`.heis.abibierpass

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class UserAdapter(
    context: Context,
    private val dataSource: ArrayList<AdminFragment.User>
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.adapter_view_user, parent, false)
        val fName = rowView.findViewById(R.id.txt_fName) as TextView
        val lName = rowView.findViewById(R.id.txt_lName) as TextView
        val vulgo = rowView.findViewById(R.id.txt_vulgo) as TextView
        val permission = rowView.findViewById(R.id.txt_permission) as TextView

        fName.text = dataSource[position].fName
        lName.text = dataSource[position].lName
        vulgo.text = dataSource[position].vulgo
        permission.text = CustomConvert().permissionToString(dataSource[position].permission)

        return rowView
    }
}