package br.org.ascic.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;

public class MainActivity extends Activity {
    private MemberDb db;
    private LinearLayout list;
    private EditText search;
    private TextView count;
    private final int green = Color.rgb(23, 74, 61);

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new MemberDb(this);
        buildScreen();
        refresh();
    }

    private void buildScreen() {
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(Color.rgb(248,250,249));
        LinearLayout header = new LinearLayout(this); header.setOrientation(LinearLayout.VERTICAL); header.setPadding(dp(20),dp(24),dp(20),dp(20)); header.setBackgroundColor(green);
        TextView title = new TextView(this); title.setText("ASCIC"); title.setTextColor(Color.WHITE); title.setTextSize(29); title.setTypeface(null, Typeface.BOLD);
        TextView subtitle = new TextView(this); subtitle.setText("Gestão de associados e rondas"); subtitle.setTextColor(Color.rgb(220,238,229)); subtitle.setTextSize(15);
        header.addView(title); header.addView(subtitle); root.addView(header);

        LinearLayout content = new LinearLayout(this); content.setPadding(dp(16),dp(16),dp(16),dp(12)); content.setOrientation(LinearLayout.VERTICAL);
        Button add = new Button(this); add.setText("+  Cadastrar associado"); add.setTextColor(Color.WHITE); add.setAllCaps(false); add.setBackground(tint(green)); add.setOnClickListener(v -> showAddDialog()); content.addView(add, matchWrap());
        Button rounds = new Button(this); rounds.setText("Rondas ASCIC: 00h às 06h"); rounds.setAllCaps(false); rounds.setOnClickListener(v -> showRoundsDialog()); LinearLayout.LayoutParams rp=matchWrap(); rp.topMargin=dp(8); content.addView(rounds,rp);
        search = new EditText(this); search.setSingleLine(true); search.setTextSize(16); search.setHint("Pesquisar por nome ou telefone"); search.setPadding(dp(14),0,dp(14),0); search.setBackground(tint(Color.WHITE)); LinearLayout.LayoutParams sp = matchWrap(); sp.topMargin=dp(12); content.addView(search,sp);
        count = new TextView(this); count.setTextColor(green); count.setTextSize(14); count.setPadding(0,dp(14),0,dp(8)); content.addView(count);
        ScrollView scroll = new ScrollView(this); list = new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL); scroll.addView(list); content.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        root.addView(content,new LinearLayout.LayoutParams(-1,0,1)); setContentView(root);
        search.addTextChangedListener(new TextWatcher(){ public void beforeTextChanged(CharSequence s,int st,int c,int a){} public void onTextChanged(CharSequence s,int st,int before,int count){refresh();} public void afterTextChanged(Editable e){} });
    }

    private void showRoundsDialog() {
        String message = "CENTRAL DE RONDAS ASCIC\n\n" +
                "Horário previsto: todos os dias, das 00h às 06h.\n\n" +
                "ACOMPANHAMENTO DOS CLIENTES\n" +
                "A localização ao vivo deverá ficar disponível para todos os clientes cadastrados, após entrarem com sua conta.\n\n" +
                "NOTIFICAÇÕES\n" +
                "Este protótipo pode programar avisos locais neste aparelho. Avisos de início, atualização de localização e conclusão para outros celulares dependem da conexão com o servidor online.\n\n" +
                "STATUS ATUAL: acompanhamento online ainda não configurado. Nenhuma localização está sendo transmitida nesta versão.";
        new AlertDialog.Builder(this).setTitle("Rondas ASCIC • 00h–06h").setMessage(message)
                .setNegativeButton("Fechar", null)
                .setNeutralButton("Ativar avisos locais", (d,w) -> enableRoundNotifications())
                .setPositiveButton("Configuração online", (d,w) -> showOnlineSetupInfo()).show();
    }

    private void showOnlineSetupInfo() {
        String message = "Para habilitar o mapa em tempo real com segurança, o ASCIC precisa de um projeto de servidor (por exemplo, Firebase), login para os clientes cadastrados, conta de operador da ronda, regras de acesso e permissões de localização.\n\n" +
                "O projeto atual não possui essas credenciais e não envia localização. Não exibiremos uma localização fictícia.\n\n" +
                "Depois de configurar o servidor, os clientes autenticados poderão ver a posição da equipe enquanto a ronda estiver ativa e receber notificações de início e conclusão.";
        new AlertDialog.Builder(this).setTitle("Ativar acompanhamento online")
                .setMessage(message).setPositiveButton("Entendi", null).show();
    }

    private void enableRoundNotifications() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 701);
            return;
        }
        RoundAlarmReceiver.scheduleNext(this);
        new AlertDialog.Builder(this).setTitle("Notificações programadas")
                .setMessage("Os avisos das rondas estão programados para o período das 00h às 06h neste aparelho.")
                .setPositiveButton("OK", null).show();
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 701 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            RoundAlarmReceiver.scheduleNext(this);
            new AlertDialog.Builder(this).setTitle("Notificações ativadas")
                    .setMessage("Você receberá avisos das rondas entre 00h e 06h neste aparelho.")
                    .setPositiveButton("OK", null).show();
        } else if (requestCode == 701) {
            new AlertDialog.Builder(this).setMessage("A permissão de notificações não foi concedida. Ative-a nas configurações do Android para receber avisos.")
                    .setPositiveButton("OK", null).show();
        }
    }

    private void showAddDialog() {
        LinearLayout form = new LinearLayout(this); form.setOrientation(LinearLayout.VERTICAL); form.setPadding(dp(4),dp(4),dp(4),dp(4));
        EditText name = field("Nome completo *", form);
        EditText phone = field("Telefone", form);
        EditText address = field("Endereço / comunidade", form);
        EditText joined = field("Data de cadastro (ex.: 18/10/2026)", form);
        EditText notes = field("Observações", form);
        ScrollView sc = new ScrollView(this); sc.addView(form);
        new AlertDialog.Builder(this).setTitle("Novo associado").setView(sc)
          .setNegativeButton("Cancelar", (d,w)->{})
          .setPositiveButton("Salvar", (d,w)->{
              String n=name.getText().toString().trim();
              if(n.isEmpty()){ new AlertDialog.Builder(this).setMessage("Informe o nome do associado.").setPositiveButton("OK",null).show(); return; }
              db.add(n, phone.getText().toString().trim(), address.getText().toString().trim(), joined.getText().toString().trim(), notes.getText().toString().trim());
              refresh();
          }).show();
    }

    private EditText field(String hint, LinearLayout parent) {
        EditText e = new EditText(this); e.setHint(hint); e.setTextSize(16); e.setSingleLine(false); e.setPadding(dp(12),dp(8),dp(12),dp(8));
        LinearLayout.LayoutParams p = matchWrap(); p.bottomMargin=dp(7); parent.addView(e,p); return e;
    }

    private void refresh() {
        if(list==null) return;
        list.removeAllViews(); Cursor c = db.all(search==null ? "" : search.getText().toString()); int n=0;
        while(c.moveToNext()) {
            n++; final long id=c.getLong(0); String name=c.getString(1); String phone=c.getString(2); String address=c.getString(3); String joined=c.getString(4); String notes=c.getString(5);
            LinearLayout card = new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(15),dp(12),dp(12),dp(10)); card.setBackground(tint(Color.WHITE)); LinearLayout.LayoutParams cp=matchWrap(); cp.bottomMargin=dp(10); card.setLayoutParams(cp);
            TextView nm = new TextView(this); nm.setText(name==null?"(Sem nome)":name); nm.setTextColor(green); nm.setTextSize(18); nm.setTypeface(null,Typeface.BOLD); card.addView(nm);
            StringBuilder info = new StringBuilder(); if(!empty(phone)) info.append("Telefone: ").append(phone).append("\n"); if(!empty(address)) info.append("Comunidade/endereço: ").append(address).append("\n"); if(!empty(joined)) info.append("Cadastro: ").append(joined).append("\n"); if(!empty(notes)) info.append("Observações: ").append(notes);
            TextView details = new TextView(this); details.setText(info.length()==0?"Sem informações adicionais":info.toString().trim()); details.setTextColor(Color.DKGRAY); details.setTextSize(14); details.setPadding(0,dp(5),0,dp(6)); card.addView(details);
            Button del = new Button(this); del.setText("Excluir cadastro"); del.setAllCaps(false); del.setTextSize(13); del.setOnClickListener(v -> new AlertDialog.Builder(this).setMessage("Deseja excluir o cadastro de "+name+"?").setNegativeButton("Cancelar",null).setPositiveButton("Excluir",(di,w)->{db.delete(id);refresh();}).show()); card.addView(del, new LinearLayout.LayoutParams(-2,dp(42)));
            list.addView(card);
        }
        c.close(); count.setText(n+ (n==1 ? " associado cadastrado" : " associados cadastrados"));
        if(n==0){TextView empty=new TextView(this);empty.setText("Nenhum associado encontrado.\nToque em ‘Cadastrar associado’ para começar.");empty.setTextColor(Color.GRAY);empty.setGravity(Gravity.CENTER);empty.setPadding(dp(16),dp(28),dp(16),dp(28));list.addView(empty);}
    }
    private boolean empty(String s){return s==null||s.trim().isEmpty();}
    private GradientDrawable tint(int color){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(12));d.setStroke(dp(1),Color.rgb(225,233,228));return d;}
    private LinearLayout.LayoutParams matchWrap(){return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);}
    private int dp(int value){return (int)(value*getResources().getDisplayMetrics().density+0.5f);}
    @Override protected void onDestroy(){if(db!=null)db.close();super.onDestroy();}
}
