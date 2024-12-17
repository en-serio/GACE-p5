package gace.controlador;


import gace.modelo.*;
import gace.modelo.dao.DAOFactory;
import gace.vista.VistaSocios;
import gace.vista.DatosUtil;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.FXML;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.Text;

public class SocioControlador {
    private VistaSocios vistaSocios;
    private DatosUtil datosUtil;
    private int fakeID = 1;
    @FXML
    private TableView<Socio> tablaSocios;

    @FXML
    private TableColumn<Socio, Integer> columnaID;

    @FXML
    private TableColumn<Socio, String> columnaNombre;

    @FXML
    private TableColumn<Socio, String> columnaApellido;

    @FXML
    private TableColumn<Socio, String> columnaTipo;

    private ObservableList<Socio> listaSocios;

    public void initialize() {
        // Configurar las columnas para que apunten a los getters de la clase Socio
        columnaID.setCellValueFactory(new PropertyValueFactory<>("idSocio"));
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        columnaTipo.setCellValueFactory(new PropertyValueFactory<>("tipoSocio"));

        // Cargar los socios falsos en la tabla
        cargarTablaSocios();
    }

    private void cargarTablaSocios() {
        List<Socio> socios = DAOFactory.getSocioDao().listar();
        listaSocios = FXCollections.observableArrayList(socios);

        tablaSocios.setItems(listaSocios);
        tablaSocios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarDetalle(newSelection);
            }
        });
    }

    public void mostrarDetalle(Socio soc){
        GridPane grid = null;
        //VBox o posem StackPane?
        if(soc instanceof SocioEstandar){
            grid = mostrarSocioEst((SocioEstandar) soc);
        }else if(soc instanceof SocioFederado){
            grid = mostrarSocioFed((SocioFederado) soc);
        }else if(soc instanceof SocioInfantil){
            grid = mostrarSocioInf((SocioInfantil) soc);
        }
        if(grid == null){
            return;
        }
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Detalles Socio " + soc.getIdSocio());


//        Button modificarSocio = new Button("Modificar Socio");
//        modificarSocio.setOnAction(event -> {
//            nuevoSocio();
//            cargarTablaSocios();
//            modalStage.close();
//        });


        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

//        HBox buttonBox = new HBox(10, aceptarButton, modificarExcursio);
//        buttonBox.setPadding(new Insets(10));
//        grid.add(buttonBox, 1, 5);

        Scene scene = new Scene(grid, 400, 250);
        modalStage.setScene(scene);
        modalStage.showAndWait();
    }

    private GridPane mostrarSocioEst(SocioEstandar soc){
        GridPane grid = new GridPane();
        grid.add(new Label("ID: " ), 0, 0);
        grid.add(new Label(String.valueOf(soc.getIdSocio())), 1, 0);

        grid.add(new Label("Nombre: "), 0, 1);
        grid.add(new Label(soc.getNombre()), 1, 1);

        grid.add(new Label("Apellido: "), 0, 2);
        grid.add(new Label(soc.getApellido()), 1, 2);

        grid.add(new Label("Nif: "), 0, 3);
        grid.add(new Label(soc.getNif()), 1, 3);

        grid.add(new Label("Tipo de Seguro: "), 0, 4);
        grid.add( new Label((soc.getSeguro().isTipo() ? "Completo" : "Estándar") + " - (" + soc.getSeguro().getPrecio()+")"), 1, 4);
        return grid;
    }
    private GridPane mostrarSocioFed(SocioFederado soc){
        GridPane grid = new GridPane();
        grid.add(new Label("ID: " ), 0, 0);
        grid.add(new Label(String.valueOf(soc.getIdSocio())), 1, 0);

        grid.add(new Label("Nombre: "), 0, 1);
        grid.add(new Label(soc.getNombre()), 1, 1);

        grid.add(new Label("Apellido: "), 0, 2);
        grid.add(new Label(soc.getApellido()), 1, 2);

        grid.add(new Label("Nif: "), 0, 3);
        grid.add(new Label(soc.getNif()), 1, 3);

        grid.add(new Label("Tipo de Seguro: "), 0, 4);
        grid.add( new Label((soc.getFederacion().getCodigo()) + " - " + soc.getFederacion().getNombre()), 1, 4);
        return grid;
    }
    private GridPane mostrarSocioInf(SocioInfantil soc){
        GridPane grid = new GridPane();
        grid.add(new Label("ID: " ), 0, 0);
        grid.add(new Label(String.valueOf(soc.getIdSocio())), 1, 0);

        grid.add(new Label("Nombre: "), 0, 1);
        grid.add(new Label(soc.getNombre()), 1, 1);

        grid.add(new Label("Apellido: "), 0, 2);
        grid.add(new Label(soc.getApellido()), 1, 2);

        grid.add(new Label("Tutor: "), 0, 3);
        grid.add(new Label(String.valueOf(soc.getNoTutor())), 1, 3);
        grid.add(new Label(""), 0, 4);
        return grid;
    }

    @FXML
    private void handleBuscar(ActionEvent event) {
        // Crear un cuadro de entrada para que el usuario ingrese el ID del socio a buscar
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Buscar Socio");
        dialog.setHeaderText("Por favor, ingrese el ID del socio a buscar:");
        dialog.setContentText("ID del Socio:");

        // Mostrar el diálogo y obtener el ID ingresado por el usuario
        dialog.showAndWait().ifPresent(idInput -> {
            try {
                // Convertir el ID ingresado a un número
                int idSocio = Integer.parseInt(idInput);

                // Buscar el socio en la lista usando el ID
                Socio socioEncontrado = listaSocios.stream()
                        .filter(socio -> socio.getIdSocio() == idSocio)
                        .findFirst()
                        .orElse(null);

                // Mostrar los detalles del socio si se encuentra
                if (socioEncontrado != null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Socio Encontrado");
                    alert.setHeaderText("Detalles del Socio:");
                    alert.setContentText(
                            "ID: " + socioEncontrado.getIdSocio() + "\n" +
                                    "Nombre: " + socioEncontrado.getNombre() + "\n" +
                                    "Apellido: " + socioEncontrado.getApellido() + "\n" +
                                    "Tipo: " + getTipoSocio(socioEncontrado)
                    );
                    alert.showAndWait();
                } else {
                    // Si no se encuentra el socio
                    datosUtil.mostrarError("Socio no encontrado No se encontró un socio con el ID " + idSocio + ".");
                }

            } catch (NumberFormatException e) {
                // Si el ID no es un número válido
                datosUtil.mostrarError("ID no válido El ID ingresado no es válido.");
            }
        });
    }



    @FXML
    private void handleRegistrar(ActionEvent event) {
        // Crear los campos de entrada para el nombre, apellido y tipo de socio
        TextField nombreField = new TextField();
        TextField apellidoField = new TextField();
        TextField nifField = new TextField();
        /* per estandar */
        TextField precioField = new TextField();
        ComboBox<String> tipoSeguro = new ComboBox<>();
        tipoSeguro.getItems().addAll("COMPLETO", "ESTÁNDAR");
        /* per federat */
        TextField codigoField = new TextField();
        TextField nombreFedField = new TextField();
        /* per infantil */
        TextField tutorField = new TextField();
        ComboBox<String> tipoSocioCombo = new ComboBox<>();
        tipoSocioCombo.getItems().addAll("ESTÁNDAR", "FEDERADO", "INFANTIL");

        Dialog<String> dialog = new Dialog<>();
        dialog.setHeight(400);
        dialog.setWidth(500);
        dialog.setTitle("Registrar Socio");
        dialog.setHeaderText("Por favor ingrese los datos del socio:");

        ButtonType buttonTypeOk = new ButtonType("Registrar", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

        VBox vbox = new VBox(10);
        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(new Label("Nombre:"), nombreField, new Label("Apellido:"), apellidoField);
        vbox.getChildren().addAll(hbox, new Label("Tipo de Socio:"), tipoSocioCombo);
        dialog.getDialogPane().setContent(vbox);

        VBox infoExtra = new VBox(10);
        VBox contenido = new VBox(10);
        contenido.getChildren().add(vbox);
        tipoSocioCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals(oldValue)){
                return;
            }
            infoExtra.getChildren().clear();
            switch (newValue){
                case "ESTÁNDAR":
                    HBox hboxInfo = new HBox(10);
                    HBox hboxOption = new HBox(10);
                    hboxInfo.getChildren().addAll(new Label("NIF:"), nifField, new Label("Precio:"), precioField);
                    hboxOption.getChildren().addAll(new Label("Tipo de Seguro:"), tipoSeguro);
                    infoExtra.getChildren().addAll(hboxInfo, hboxOption);
                    break;
                case "FEDERADO":
                    infoExtra.getChildren().addAll(new Label("NIF:"), nifField,
                            new Label("Código Fed:"), codigoField,
                            new Label("Nombre Fed:"), nombreFedField);
                    break;
                case "INFANTIL":
                    infoExtra.getChildren().addAll(
                            new Label("Tutor:"), tutorField);
                    break;
            }
            contenido.getChildren().add(infoExtra);
        });
        dialog.getDialogPane().setContent(contenido);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonTypeOk) {
                String nombre = nombreField.getText();
                String apellido = apellidoField.getText();
                if (nombre.isEmpty() || apellido.isEmpty() || tipoSocioCombo.getValue() == null ) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Por favor, complete todos los campos.");
                    alert.show();
                    return null;
                }

                switch (tipoSocioCombo.getValue()){
                    case "ESTÁNDAR":
                        String nifEst = nifField.getText();
                        double precio = Double.parseDouble(precioField.getText());
                        boolean tipo = tipoSeguro.getValue().equals("COMPLETO");
                        if (nifEst.isEmpty() || precio == 0 ) {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Por favor, complete todos los campos.");
                            alert.show();
                            return null;
                        }
                        Seguro seg = new Seguro(tipo, precio);
                        DAOFactory.getSeguroDao().insertar(seg);
                        DAOFactory.getSocioDao().insertar(new SocioEstandar(nombre, apellido, nifEst, seg));
                        break;
                    case "FEDERADO":
                        String nifFed = nifField.getText();
                        String codigo = codigoField.getText();
                        String nombreFed = nombreFedField.getText();
                        if (nifFed.isEmpty() || codigo.isEmpty() ) {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Por favor, complete todos los campos.");
                            alert.show();
                            return null;
                        }
                        Federacion fed = new Federacion(codigo, nombreFed);
                        DAOFactory.getFederacionDao().insertar(fed);
                        DAOFactory.getSocioDao().insertar(new SocioFederado(nombre, apellido, nifFed, fed));
                        break;
                    case "INFANTIL":
                        int tutor = Integer.parseInt(tutorField.getText());
                        if (tutor == 0 ) {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Por favor, complete todos los campos.");
                            alert.show();
                            return null;
                        }
                        DAOFactory.getSocioDao().insertar(new SocioInfantil(nombre, apellido, tutor));
                        break;
                }
                cargarTablaSocios();
            }
            return null;
        });

        dialog.showAndWait();
    }
    @FXML
    private void handleEliminar(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Eliminar Socio");
        dialog.setHeaderText("Por favor, ingrese el ID del socio a eliminar:");
        dialog.setContentText("ID del Socio:");

        // Mostrar el diálogo y obtener el ID ingresado por el usuario
        dialog.showAndWait().ifPresent(idInput -> {
            // Intentamos convertir el ID ingresado a un número
            try {
                int idSocio = Integer.parseInt(idInput);  // Convertir el ID ingresado a un entero

                // Buscar el socio en la lista usando el ID
                Socio socioAEliminar = null;
                for (Socio socio : listaSocios) {
                    if (socio.getIdSocio() == idSocio) {
                        socioAEliminar = socio;
                        break;
                    }
                }

                // Si encontramos el socio con el ID, lo eliminamos
                if (socioAEliminar != null) {
                    listaSocios.remove(socioAEliminar);  // Eliminar de la lista
                    tablaSocios.setItems(listaSocios);  // Actualizar la tabla
                    datosUtil.mostrarError("Socio eliminado El socio con ID " + idSocio + " ha sido eliminado.");
                } else {
                    // Si no se encuentra el socio
                    datosUtil.mostrarError("Socio no encontrado No se encontró un socio con el ID " + idSocio + ".");
                }

            } catch (NumberFormatException e) {
                // Si el ID no es un número válido
                datosUtil.mostrarError("ID no válido El ID ingresado no es válido.");
            }
        });
    }

    @FXML
    private void handleModificar(ActionEvent event) {
        // Crear un cuadro de entrada para que el usuario ingrese el ID del socio a modificar
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Modificar Socio");
        dialog.setHeaderText("Por favor, ingrese el ID del socio a modificar:");
        dialog.setContentText("ID del Socio:");

        // Mostrar el diálogo y obtener el ID ingresado por el usuario
        dialog.showAndWait().ifPresent(idInput -> {
            try {
                // Convertir el ID ingresado a un número
                int idSocio = Integer.parseInt(idInput);

                // Buscar el socio en la lista
                Socio socioAModificar = listaSocios.stream().filter(socio -> socio.getIdSocio() == idSocio).findFirst().orElse(null);

                // Si el socio se encuentra
                if (socioAModificar != null) {
                    // Crear los campos de entrada con los datos actuales del socio
                    TextField nombreField = new TextField(socioAModificar.getNombre());
                    TextField apellidoField = new TextField(socioAModificar.getApellido());
                    ComboBox<String> tipoSocioCombo = new ComboBox<>();
                    tipoSocioCombo.getItems().addAll("ESTÁNDAR", "FEDERADO", "INFANTIL");
                    tipoSocioCombo.setValue(getTipoSocio(socioAModificar));

                    // Crear el diálogo
                    Dialog<String> modificarDialog = new Dialog<>();
                    modificarDialog.setTitle("Modificar Socio");
                    modificarDialog.setHeaderText("Modifique los datos del socio:");

                    ButtonType buttonTypeOk = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
                    ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                    modificarDialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

                    VBox vbox = new VBox(10);
                    vbox.getChildren().addAll(new Label("Nombre:"), nombreField,
                            new Label("Apellido:"), apellidoField,
                            new Label("Tipo de Socio:"), tipoSocioCombo);
                    modificarDialog.getDialogPane().setContent(vbox);


//                    modificarDialog.showAndWait().ifPresent(result -> {
//                        String nuevoNombre = nombreField.getText();
//                        String nuevoApellido = apellidoField.getText();
//                        String nuevoTipoSocio = tipoSocioCombo.getValue();
//                            // Actualizar el socio existente con los nuevos datos
//                            socioAModificar.setNombre(nuevoNombre);
//                            socioAModificar.setApellido(nuevoApellido);
//
//                            // Si el tipo de socio cambia, convertirlo al nuevo tipo
//                            if (!getTipoSocio(socioAModificar).equals(nuevoTipoSocio)) {
//                                Socio socioNuevo = null;
//                                switch (nuevoTipoSocio) {
//                                    case "ESTÁNDAR":
//                                        socioNuevo = new SocioEstandar(socioAModificar.getIdSocio(), nuevoNombre, nuevoApellido, "NIF123", new Seguro(1, true, 40));
//                                        break;
//                                    case "FEDERADO":
//                                        socioNuevo = new SocioFederado(socioAModificar.getIdSocio(), nuevoNombre, nuevoApellido, "NIF456", new Federacion("020", "0202"));
//                                        break;
//                                    case "INFANTIL":
//                                        socioNuevo = new SocioInfantil(socioAModificar.getIdSocio(), nuevoNombre, nuevoApellido, 123);
//                                        break;
//                                }
//
//                                // Reemplazar en la lista
//                                listaSocios.set(listaSocios.indexOf(socioAModificar), socioNuevo);
//                            }
//
//                            // Actualizar la tabla
//                            tablaSocios.setItems(listaSocios);
//                            tablaSocios.refresh();  // Forzar actualización de la tabla
//                        }
//                    });

                } else {
                    datosUtil.mostrarError("Socio no encontrado No se encontró un socio con el ID " + idSocio + ".");
                }
            } catch (NumberFormatException e) {
                datosUtil.mostrarError("ID no válido El ID ingresado no es válido.");
            }
        });
    }

    // Método auxiliar para determinar el tipo de socio como String
    private String getTipoSocio(Socio socio) {
        if (socio instanceof SocioEstandar) {
            return "ESTÁNDAR";
        } else if (socio instanceof SocioFederado) {
            return "FEDERADO";
        } else if (socio instanceof SocioInfantil) {
            return "INFANTIL";
        }
        return "";
    }



    public SocioControlador(VistaSocios vistaSocios) {
        this.vistaSocios = vistaSocios;
        this.datosUtil = new DatosUtil();
    }

    public SocioControlador() {
        this.vistaSocios = new VistaSocios();
        this.datosUtil = new DatosUtil();
    }

    public int nouSoci(){
        String strSocio = vistaSocios.formSocio();
        if (strSocio == null) {
            datosUtil.mostrarError("Error al crear el socio");
            return 0;
        }
        String[] datosSocio = strSocio.split(",");
        if (datosSocio.length < 3) {
            datosUtil.mostrarError("Datos del socio incompletos");
            return 0;
        }
        int tipoSocio = Integer.parseInt(datosSocio[0]);
        int id = 0;
        switch (tipoSocio) {
            //EST
            case 1:
                SocioEstandar socioEst = nouSociEstandar(datosSocio[1], datosSocio[2]);
                if (socioEst == null) {
                    datosUtil.mostrarError("Error al crear el socio estándar");
                    return 0;
                }
                DAOFactory.getSocioDao().insertar(socioEst);
                break;
            //FED
            case 2:
                SocioFederado socioFed = nouSociFederado(datosSocio[1], datosSocio[2]);
                if (socioFed == null) {
                    datosUtil.mostrarError("Error al crear el socio estándar");
                    return 0;
                }
                DAOFactory.getSocioDao().insertar(socioFed);
                break;
            //INF
            case 3:
                SocioInfantil socioInf = nouSociInfantil(datosSocio[1], datosSocio[2]);
                if (socioInf == null) {
                    datosUtil.mostrarError("Error al crear el socio infantil");
                    return 0;
                }
                DAOFactory.getSocioDao().insertar(socioInf);
                vistaSocios.mostrarSocio(socioInf.toString());
                break;
            default:
                datosUtil.mostrarError("Tipo de socio no válido");
                return 0;
        }
        return id;
    }

    public Socio crearSocio(){
        int id = nouSoci();
        return DAOFactory.getSocioDao().buscar(id);
    }


    public SocioEstandar nouSociEstandar(String nombre,String apellido){
        String nif = "1567848F";
//        if(existeNif(nif)){
//            datosUtil.mostrarError("Nif ya existe.");
//            return null;
//        }
        Seguro seg = new Seguro(true,15.5);/*nuevoSeg();*/
        DAOFactory.getSeguroDao().insertar(seg);
        DAOFactory.getSocioDao().insertar(new SocioEstandar(nombre, apellido, nif, seg));
        System.out.println("Socio creado");
        return new SocioEstandar( nombre, apellido, nif, seg);
    }

    public SocioFederado nouSociFederado(String nombre, String apellido){
        String nif = vistaSocios.formNif();
        if(nif == null){
            datosUtil.mostrarError("Nif no válido.");
            return null;
        }
        if(existeNif(nif)){
            datosUtil.mostrarError("Nif ya existe.");
            return null;
        }
        Federacion fed = pedirFed();
        if(fed == null){
            datosUtil.mostrarError("Federación no válida.");
            return null;
        }
        //DAOFactory.getFederacionDao().insertar(fed);
        return new SocioFederado(nombre, apellido, nif, fed);
    }

    public boolean existeNif(String nif){
        return DAOFactory.getSocioDao().hayNif(nif);
    }

    public SocioInfantil nouSociInfantil(String nombre, String apellido){
        int noTutor = vistaSocios.formTutor();
        if(noTutor == 0){
            return null;
        }
        if(!buscarTutor(noTutor)){
            return null;
        }
        return new SocioInfantil(nombre, apellido, noTutor);
    }



//    public SocioInfantil nouSociInfantil(String nombre, String apellido, int noTutor) {
//        if(noTutor == 0){
//            return null;
//        }
//        if(!buscarTutor(noTutor)){
//            return null;
//        }
//        return new SocioInfantil(nombre, apellido, noTutor);
//    }

    public boolean buscarTutor(int noTutor) {
        Socio socio = DAOFactory.getSocioEstandarDao().buscar(noTutor);
        if(socio == null){
            socio = DAOFactory.getSocioFederadoDao().buscar(noTutor);
            if(socio == null){
                return false;
            }
        }
        return true;
    }

    public boolean mostrarSocios(int mostrarFiltro, int filtro) {
        int opcionSocios = 0;
        if(mostrarFiltro == 1){
            opcionSocios = vistaSocios.requerirFiltro();
        }else {
            opcionSocios = filtro;
        }
        switch (opcionSocios) {
            case 1:
                //error de tipos de socio amb el socioEstandarDao
                //list = DAOFactory.getSocioEstandarDao().listar();
                List<SocioEstandar> list = DAOFactory.getSocioEstandarDao().listar();
                if(list == null){
                    datosUtil.mostrarError("No hay socios estándar");
                    return false;
                }
                for(Socio socio : list) {
                    vistaSocios.mostrarSocio(socio.toString());
                }
                break;
            case 2:
                List<SocioFederado> listFed = DAOFactory.getSocioFederadoDao().listar();
                if(listFed == null){
                    datosUtil.mostrarError("No hay socios federados");
                    return false;
                }
                for(Socio socio : listFed) {
                    vistaSocios.mostrarSocio(socio.toString());
                }
                break;
            case 3:
                List<SocioInfantil> listInf = DAOFactory.getSocioInfantilDao().listar();
                if(listInf == null){
                    datosUtil.mostrarError("No hay socios infantiles");
                    return false;
                }
                for(Socio socio : listInf) {
                    vistaSocios.mostrarSocio(socio.toString());
                }
                break;
            case 4:
                List<Socio> todos = DAOFactory.getSocioDao().listar();
                if(todos == null){
                    datosUtil.mostrarError("No hay socios");
                    return false;
                }
                for(Socio socio : todos) {
                    vistaSocios.mostrarSocio(socio.toString());
                }
                break;
            case 0:
                break;
            default:
                datosUtil.mostrarError("Opción no válida. Intente de nuevo.");
        }
        return true;
    }

    public Socio buscarSocio(int noSocio) {
        return DAOFactory.getSocioDao().buscar(noSocio);
    }


    public Federacion pedirFed(){
        int accion = datosUtil.pedirOpcion("¿Desea seleccionar una federación ya existente o crear una nueva?", "Seleccionar", "Crear nueva");
        if(accion == -1){
            return null;
        }
        if(accion == 1){
            Federacion fed = seleccionarFed();
            if(fed != null){
                return fed;
            }
        }
        return nuevaFed();
    }

    public Federacion seleccionarFed(){
        ArrayList<Federacion> fedes = null;
        fedes = DAOFactory.getFederacionDao().listar();
        if(fedes.isEmpty()){
            datosUtil.mostrarError("No hay federaciones");
            return null;
        }else{
            for(Federacion fed : fedes){
                vistaSocios.mostrarSocio(fed.toString());
            }
            String codigo = datosUtil.devString("Introduce el código de la federación");
            if(codigo == null){
                return null;
            }
            return DAOFactory.getFederacionDao().buscar(codigo);
        }
    }
    public Federacion nuevaFed(){
        String fed = vistaSocios.formFederacion();
        String[] datosFed = fed.split(",");
        if (datosFed.length < 2) {
            datosUtil.mostrarError("Datos de la federación incompletos");
            return null;
        }
        Federacion federacion = new Federacion(datosFed[0], datosFed[1]);
        DAOFactory.getFederacionDao().insertar(federacion);
        return federacion;
    }

    public Seguro nuevoSeg(){
        String seg = vistaSocios.formSeguro();
        if (seg == null) {
            return null;
        }
        String[] datosSeg = seg.split(",");
        if (datosSeg.length < 2) {
            datosUtil.mostrarError("Datos del seguro incompletos");
            return null;
        }
        boolean tipo = Integer.parseInt(datosSeg[0]) == 1;
        return new Seguro(tipo, Double.parseDouble(datosSeg[1]));
    }

//    public boolean seleccionarSocio(ArrayList<Socio> socios){
//        String codigo = vistaSocios.pedirSocio();
//        for(Socio socio : socios){
//            if(socio.getNoSocio().equals(codigo)) {
//                vistaSocios.mostrarSocio("Es este el socio que desea eliminar " + socio.toString() + "?");
//                if (vistaSocios.confirmar()) {
//                    listaSocios.getListaSocios().remove(socio);
//                    datosUtil.mostrarError("Socio eliminado");
//                    return true;
//                }
//                return false;
//            }
//        }
//        return false;
//    }

    public boolean pedirSocio(){
        Socio socio = obtenerSocio();
        if(socio == null){
            return false;
        }
        vistaSocios.mostrarSocio(socio.toString());
        return true;
    }

    public boolean eliminarSocio(){
        Socio socio = obtenerSocio();
        if(socio == null){
            return false;
        }
        vistaSocios.mostrarSocio(socio.toString());
        List<Inscripcion> insc = null;
        if(socio instanceof SocioEstandar) {
            insc = DAOFactory.getInscripcionDao().ListarXSocioEst(socio);
        }else if(socio instanceof SocioFederado){
            insc = DAOFactory.getInscripcionDao().ListarXSocioFed(socio);
        }else{
            insc = DAOFactory.getInscripcionDao().ListarXSocioInf(socio);
        }
        if(insc != null){
            datosUtil.mostrarError("No se puede eliminar el socio, tiene inscripciones");
            return false;
        }
        if(vistaSocios.confirmar("¿Está seguro de que desea eliminar este socio?")){
            DAOFactory.getSocioDao().eliminar(socio.getIdSocio());
            datosUtil.mostrarError("Socio eliminado");
            return true;
        }
        return false;
    }

    public Socio obtenerSocio(){
        int formaBuscar = datosUtil.pedirOpcion("¿Como desea buscar?", "NIF", "Número de socio");
        int opcion = datosUtil.pedirOpcion("Deseas disponer de ayudas?","Sí","No");
        if(opcion == 1){
            mostrarSocios(0,4);
        }
        if (formaBuscar == -1) {
            return null;
        }
        Socio socio = null;
        if(formaBuscar == 1){
            socio = buscarNIF();
        }else{
            socio = buscarNoSocio();
        }
        if(socio == null){
            return null;
        }
        return socio;
    }

    public Socio buscarNoSocio(){
        int noSocio = vistaSocios.pedirSocio();
        if(noSocio == 0){
            return null;
        }
        Socio socio = DAOFactory.getSocioDao().buscar(noSocio);
        if(socio == null){
            datosUtil.mostrarError("Socio no encontrado");
            return null;
        }
        return socio;
    }

    public Socio buscarNIF(){
        String nif = vistaSocios.pedirNif();
        if(nif == null){
            return null;
        }
        Socio socio = DAOFactory.getSocioDao().buscar(nif);
        if(socio == null){
            datosUtil.mostrarError("Socio no encontrado");
            return null;
        }
        return socio;
    }

    public boolean modificarSeguro(){
        Socio socio = obtenerSocio();
        if(socio == null){
            return false;
        }
        if(!(socio instanceof SocioEstandar)){
            datosUtil.mostrarError("Error Socio no Estandar");
            return false;
        }
        String strSeg = vistaSocios.formSeguro();
        if (strSeg == null) {
            return false;
        }
        String[] datosSeg = strSeg.split(",");
        if (datosSeg.length < 2) {
            datosUtil.mostrarError("Datos del seguro incompletos");
            return false;
        }
        boolean tipo = Integer.parseInt(datosSeg[0]) == 1;
        Seguro seg = new Seguro(tipo, Double.parseDouble(datosSeg[1]));
        DAOFactory.getSeguroDao().insertar(seg);
        ((SocioEstandar) socio).setSeguro(seg);
        DAOFactory.getSocioEstandarDao().modificar((SocioEstandar) socio);
        return true;
    }
    public boolean modificarFederacion(){
        Socio socio = obtenerSocio();
        if(socio == null){
            return false;
        }
        if(!(socio instanceof SocioFederado)){
            datosUtil.mostrarError("Error socio no Federado");
            return false;
        }
        Federacion fed = pedirFed();
        ((SocioFederado) socio).setFederacion(fed);
        DAOFactory.getSocioFederadoDao().modificar((SocioFederado) socio);
        return true;
    }
}
