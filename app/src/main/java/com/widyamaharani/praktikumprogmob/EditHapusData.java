package com.widyamaharani.praktikumprogmob;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class EditHapusData extends AppCompatActivity {
    String nama_kategori = "Pengeluaran";
    SeekBar sbPrioritas;
    TextView tvLabelprioritas, tvLabeluang;
    EditText etUang, etTanggal, etDeskripsi;
    RadioGroup rgKategori;
    LinearLayout prioritas;
    CheckBox cbVerifikasi;
    Calendar calendar = Calendar.getInstance();
    RadioButton pemasukan, pengeluaran;
    int point=1, kategori=0;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hapus_data);
        prioritas = findViewById(R.id.prioritas);
        sbPrioritas = findViewById(R.id.sbPrioritas);
        tvLabelprioritas = findViewById(R.id.tvLabelprioritas);
        tvLabeluang = findViewById(R.id.tvLabeluang);
        etUang = findViewById(R.id.etUang);
        rgKategori = findViewById(R.id.rgKategori);
        etTanggal = findViewById(R.id.etTanggal);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        cbVerifikasi = findViewById(R.id.cbVerifikasi);
        pemasukan = findViewById(R.id.rbPemasukan);
        pengeluaran = findViewById(R.id.rbPengeluaran);

        String radiokategori = getIntent().getExtras().getString("kategori");
        if(radiokategori.equals("Pemasukkan")){
            pemasukan.setChecked(true);
            prioritas.setVisibility(View.GONE);
        }

        tvLabelprioritas.setText(getIntent().getExtras().getString("skala"));
        etUang.setText(getIntent().getExtras().getString("jumlah"));
        etTanggal.setText(getIntent().getExtras().getString("tanggal"));
        etDeskripsi.setText(getIntent().getExtras().getString("deskripsi"));

        etTanggal.setOnClickListener(v -> Show_Calendar());
        if(getIntent().getExtras()!=null) {
            if (getIntent().getExtras().getString("message") != null) {
                Toast.makeText(this, getIntent().getExtras().getString("message"), Toast.LENGTH_LONG).show();
            }
        }
        sbPrioritas.setMax(9);
        sbPrioritas.setProgress(Integer.parseInt(tvLabelprioritas.getText().toString()));
        sbPrioritas.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                point=i+1;
                tvLabelprioritas.setText(String.valueOf(point));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        rgKategori.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i){
                case R.id.rbPengeluaran:
                    tvLabeluang.setText("Uang Pengeluaran");
                    etUang.setHint("Masukan jumlah uang pengeluaran");
                    prioritas.setVisibility(View.VISIBLE);
                    kategori=0;
                    nama_kategori = "Pengeluaran";
                    break;
                case R.id.rbPemasukan:
                    tvLabeluang.setText("Uang Pemasukan");
                    etUang.setHint("Masukan jumlah uang pemasukan");
                    prioritas.setVisibility(View.GONE);
                    kategori=1;
                    nama_kategori = "Pemasukan";
                    break;
            }
        });
}

    private void Show_Calendar() {
        etTanggal.setShowSoftInputOnFocus(false);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view1, year, month, dayOfMonth) -> {
                    month = month + 1;
                    String date;
                    String m,d;
                    if (month < 10) {
                        m="0"+month;
                    } else {
                        m= String.valueOf(month);
                    }
                    if (dayOfMonth < 10) {
                        d="0"+dayOfMonth;
                    } else {
                        d= String.valueOf(dayOfMonth);
                    }
                    date = d + "-" + m + "-" + year;
                    etTanggal.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        if(!datePickerDialog.isShowing()) {
            datePickerDialog.show();
        }
    }

    public void edit(View view) {
        id = Integer.parseInt(getIntent().getExtras().getString("id"));
        if(etUang.getText().toString().isEmpty()||
                etDeskripsi.getText().toString().isEmpty()||
                etTanggal.getText().toString().isEmpty()){
            Toast.makeText(this, "Isi Semua Field", Toast.LENGTH_LONG).show();
        }else if(!cbVerifikasi.isChecked()){
            Toast.makeText(this, "Input verifikasi terlebih dahulu", Toast.LENGTH_LONG).show();
        }else {
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            SetterGetterData sgd = new SetterGetterData();
            sgd.setKategori(nama_kategori);
            sgd.setUang(Integer.parseInt(etUang.getText().toString()));
            sgd.setDeskripsi(etDeskripsi.getText().toString());
            sgd.setTanggal(etTanggal.getText().toString());
            if (kategori == 0) {
                sgd.setPrioritas(point);
            } else {
                sgd.setPrioritas(0);
            }
            boolean edit = dbHelper.perbaharuiTransaksi(sgd, id);
            if (edit) {
                Toast.makeText(getApplicationContext(), "Transaksi berhasil diperbaharui", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Kesalahan terjadi!", Toast.LENGTH_SHORT).show();
            }
            dbHelper.close();
            Intent intent = new Intent(EditHapusData.this, ViewData.class);
            startActivity(intent);
        }
    }

    public void hapus(View view){
        String radiokategori = getIntent().getExtras().getString("kategori");
        String message = "Yakin menghapus data " +radiokategori+ "?";
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Hapus Data");
        alertDialogBuilder
                .setMessage(message)
                .setIcon(R.drawable.ic_baseline_description_24)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        DBHelper dbHelper = new DBHelper(getApplicationContext());
                        dbHelper.hapusTransaksi(Integer.parseInt(getIntent().getExtras().getString("id")));
                        Toast.makeText(getApplicationContext(), "Transaksi berhasil dihapus", Toast.LENGTH_SHORT).show();
                        dbHelper.close();

                        Intent intent = new Intent(EditHapusData.this, ViewData.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog =alertDialogBuilder.create();
        alertDialog.show();
    }
}
