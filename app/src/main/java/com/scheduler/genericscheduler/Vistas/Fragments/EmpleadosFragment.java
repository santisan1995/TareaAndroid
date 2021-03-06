package com.scheduler.genericscheduler.Vistas.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

import com.scheduler.genericscheduler.Controladores.EmpleadoAdaptador;
import com.scheduler.genericscheduler.Controladores.InterfaceServicios;
import com.scheduler.genericscheduler.Modelos.Empleado;
import com.scheduler.genericscheduler.Modelos.EmpleadoRespuesta;
import com.scheduler.genericscheduler.Modelos.RespuestaSesion;
import com.scheduler.genericscheduler.R;
import com.scheduler.genericscheduler.Vistas.Activities.HomeActivity;
import com.scheduler.genericscheduler.Vistas.Activities.PantallaCargaActivity;
import com.scheduler.genericscheduler.Vistas.Activities.PrincipalActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class EmpleadosFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ProgressDialog progressDialog;
    private static final String TAG = "Prueba";

    private EmpleadoAdaptador empleadoAdaptador;
    private ListView listViewEmpleado;
    private EmpleadoServiciosFragment empleadoServiciosFragment;
    private RespuestaSesion respuestaSesion;
    private String token;
    private String tipo;
    private android.support.v7.widget.Toolbar mToolbar;

    private OnFragmentInteractionListener mListener;

    public EmpleadosFragment() {
        // Required empty public constructor
    }
    private Retrofit retrofit;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EmpleadosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EmpleadosFragment newInstance(String param1, String param2) {
        EmpleadosFragment fragment = new EmpleadosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
     }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empleados, container, false);
        mToolbar =  view.findViewById(R.id.toolbar2);
        mToolbar.setTitle(R.string.titulo_toolbar_empleados);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TareaMoverseHome().execute();
            }
        });
        listViewEmpleado = view.findViewById(R.id.list_view_empleado);
        empleadoAdaptador = new EmpleadoAdaptador(getActivity());
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        token = prefs.getString("token","algo");
        Log.e(TAG,"token:" + token);
        new TareaCargarDatosEmpleados().execute();
        listViewEmpleado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Empleado d = (Empleado) empleadoAdaptador.getItem(position);
                getActivity().getIntent().putExtra("empleado_seleccionado",d);
                new TareaCambiarFragmentServicios().execute();
            }
        });
        return view;
    }


    private void obtenerDatos() {
        InterfaceServicios service = retrofit.create(InterfaceServicios.class);
        Call<ArrayList<Empleado>> empleadoRespuestaCall = service.ObtenerListaEmpleados(token);
        empleadoRespuestaCall.enqueue(new Callback<ArrayList<Empleado>>() {
            @Override
            public void onResponse(Call<ArrayList<Empleado>> call, Response<ArrayList<Empleado>> response) {
                ArrayList<Empleado> resp = response.body();
                empleadoAdaptador.AdicionarListaEmpleados(resp);
            }

            @Override
            public void onFailure(Call<ArrayList<Empleado>> call, Throwable t) {
                Log.e(TAG,"Errores varios");
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class TareaCargarDatosEmpleados extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Procesando...");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://18.218.149.158/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            obtenerDatos();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listViewEmpleado.setAdapter(empleadoAdaptador);
            progressDialog.dismiss();
        }
    }
    public class TareaCambiarFragmentServicios extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Procesando...");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            empleadoServiciosFragment = new EmpleadoServiciosFragment();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.pop_enter,R.anim.pop_exit);
            fragmentTransaction.replace(R.id.frame_contenedor,empleadoServiciosFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            progressDialog.dismiss();
        }
    }
    public class TareaMoverseHome extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(getActivity(),HomeActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.right_in,R.anim.right_out);
        }
    }
}
