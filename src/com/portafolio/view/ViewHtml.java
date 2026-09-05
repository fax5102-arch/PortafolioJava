package com.portafolio.view;

import com.portafolio.model.Evidencia;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class ViewHtml {

    public static String renderPortafolio(List<Evidencia> listaEvidencias, boolean autenticado) {
        StringBuilder evidenciasHtml = new StringBuilder();
        if (listaEvidencias.isEmpty()) {
            evidenciasHtml.append("<p style=\"font-size: 0.88rem;\">No hay evidencias publicadas aún.</p>");
        } else {
            Map<String, List<Evidencia>> evidenciasPorSemana = listaEvidencias.stream()
                    .collect(Collectors.groupingBy(Evidencia::getSemana, LinkedHashMap::new, Collectors.toList()));

            for (Map.Entry<String, List<Evidencia>> entry : evidenciasPorSemana.entrySet()) {
                String semana = entry.getKey();
                List<Evidencia> trabajos = entry.getValue();

                StringBuilder trabajosHtml = new StringBuilder();
                for (Evidencia ev : trabajos) {
                    String pdfBtn = (ev.getPdfUrl() != null && !ev.getPdfUrl().isEmpty())
                            ? String.format("<a href=\"%s\" target=\"_blank\" class=\"btn-pdf\">Ver Documento PDF ›</a>", escapeHtml(ev.getPdfUrl()))
                            : "<span style=\"font-size: 0.78rem; color: var(--text-muted); font-style: italic;\">Sin documento adjunto</span>";

                    trabajosHtml.append(String.format(
                            "<div style=\"background: var(--bg-main); border: 1px solid var(--border-color); border-radius: 8px; padding: 0.9rem; margin-bottom: 0.8rem;\">" +
                                    "<p style=\"font-size: 0.88rem; margin-bottom: 0.6rem; color: var(--text-white);\">%s</p>" +
                                    "%s" +
                                    "</div>",
                            escapeHtml(ev.getDescripcion()), pdfBtn
                    ));
                }

                String htmlItem = String.format(
                        "<details style=\"background: var(--inner-card-bg); border: 1px solid var(--border-color); border-radius: 12px; margin-bottom: 0.8rem; overflow: hidden; transition: border-color 0.3s ease;\">" +
                                "<summary style=\"padding: 1rem 1.2rem; cursor: pointer; color: var(--text-white); font-weight: 600; font-size: 0.95rem; display: flex; justify-content: space-between; align-items: center; user-select: none;\">" +
                                "<span>📂 %s</span>" +
                                "<span style=\"font-size: 0.75rem; color: var(--accent-cyan); font-family: monospace;\">Ver detalles ▼</span>" +
                                "</summary>" +
                                "<div style=\"padding: 1rem 1.2rem; border-top: 1px solid var(--border-color);\">" +
                                "%s" +
                                "</div>" +
                                "</details>",
                        escapeHtml(semana), trabajosHtml.toString()
                );
                evidenciasHtml.append(htmlItem);
            }
        }

        String headerBtn = autenticado
                ? "<a href=\"/cpanel\" class=\"btn-cpanel\">⚙️ Ir a cPanel</a> <a href=\"/logout\" class=\"btn-login-nav\">Cerrar Sesión</a>"
                : "<button onclick=\"abrirModal()\" class=\"btn-login-nav\">🔑 Acceso Alumno</button>";

        return "<!DOCTYPE html>\n" +
                "<html lang=\"es\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>E-Portafolio 2026</title>\n" +
                "    <style>\n" +
                "        :root {\n" +
                "            --bg-main: #060913; --card-bg: #0d1322; --inner-card-bg: #080d1a;\n" +
                "            --border-color: #172033; --accent-cyan: #00f2fe; --text-white: #ffffff; --text-muted: #94a3b8;\n" +
                "        }\n" +
                "        * { box-sizing: border-box; margin: 0; padding: 0; }\n" +
                "        @keyframes fadeIn { from { opacity: 0; transform: translateY(15px); } to { opacity: 1; transform: translateY(0); } }\n" +
                "        @keyframes scaleUp { from { opacity: 0; transform: scale(0.95); } to { opacity: 1; transform: scale(1); } }\n" +
                "        body {\n" +
                "            font-family: 'Segoe UI', system-ui, sans-serif; background-color: var(--bg-main); color: var(--text-muted);\n" +
                "            display: flex; flex-direction: column; align-items: center; min-height: 100vh; padding: 2rem; animation: fadeIn 0.6s ease-out;\n" +
                "        }\n" +
                "        .top-bar { width: 100%; max-width: 1050px; display: flex; justify-content: flex-end; gap: 0.8rem; margin-bottom: 1rem; }\n" +
                "        .btn-login-nav, .btn-cpanel {\n" +
                "            background: transparent; border: 1px solid var(--accent-cyan); color: var(--accent-cyan);\n" +
                "            padding: 0.5rem 1rem; border-radius: 8px; font-size: 0.85rem; font-weight: 600; cursor: pointer; text-decoration: none;\n" +
                "            transition: all 0.3s ease;\n" +
                "        }\n" +
                "        .btn-login-nav:hover, .btn-cpanel:hover { background: rgba(0, 242, 254, 0.15); transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0, 242, 254, 0.2); }\n" +
                "        .layout { display: grid; grid-template-columns: 300px 1fr; gap: 1.5rem; max-width: 1050px; width: 100%; }\n" +
                "        .sidebar {\n" +
                "            background: var(--card-bg); border: 1px solid var(--border-color); border-radius: 16px; padding: 2.5rem 1.5rem;\n" +
                "            display: flex; flex-direction: column; align-items: center; text-align: center;\n" +
                "        }\n" +
                "        .profile-img { width: 110px; height: 110px; border-radius: 50%; object-fit: cover; border: 3px solid var(--accent-cyan); margin-bottom: 1.2rem; }\n" +
                "        .profile-name { color: var(--text-white); font-size: 1.25rem; font-weight: 700; margin-bottom: 0.8rem; }\n" +
                "        .tag-dev { background: rgba(0, 242, 254, 0.05); color: var(--accent-cyan); border: 1px solid rgba(0, 242, 254, 0.2); padding: 0.35rem 0.9rem; border-radius: 20px; font-size: 0.8rem; font-family: monospace; margin-bottom: 2rem; }\n" +
                "        .sidebar-info { width: 100%; border-top: 1px solid var(--border-color); padding-top: 1.2rem; display: flex; flex-direction: column; gap: 0.8rem; text-align: left; font-size: 0.85rem; }\n" +
                "        .main-content { display: flex; flex-direction: column; gap: 1.5rem; }\n" +
                "        .card { background: var(--card-bg); border: 1px solid var(--border-color); border-radius: 16px; padding: 1.8rem 2rem; transition: transform 0.3s ease, border-color 0.3s ease; }\n" +
                "        .card:hover { transform: translateY(-3px); border-color: rgba(0, 242, 254, 0.3); box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5); }\n" +
                "        .section-title { color: var(--text-white); font-size: 1.2rem; font-weight: 700; margin-bottom: 1rem; display: flex; align-items: center; }\n" +
                "        .section-title::before { content: ''; display: inline-block; width: 4px; height: 18px; background: var(--accent-cyan); margin-right: 0.6rem; border-radius: 2px; }\n" +
                "        .tech-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-top: 0.5rem; }\n" +
                "        .tech-box { background: var(--inner-card-bg); border: 1px solid var(--border-color); border-radius: 12px; padding: 1.2rem; }\n" +
                "        .tech-box-title { color: var(--accent-cyan); font-size: 0.75rem; font-weight: 700; text-transform: uppercase; margin-bottom: 1rem; }\n" +
                "        .badges-container { display: flex; flex-wrap: wrap; gap: 0.5rem; }\n" +
                "        .badge-item { background: rgba(255, 255, 255, 0.05); color: var(--text-white); border: 1px solid var(--border-color); padding: 0.35rem 0.75rem; border-radius: 6px; font-size: 0.8rem; }\n" +
                "        .btn-pdf { display: inline-flex; align-items: center; gap: 0.5rem; background: linear-gradient(90deg, #00c6ff 0%, #0072ff 100%); color: #fff; font-weight: 600; padding: 0.5rem 1rem; border-radius: 6px; text-decoration: none; font-size: 0.8rem; margin-top: 0.2rem; }\n" +
                "        details[open] summary { border-bottom: 1px solid var(--border-color); background: rgba(0, 242, 254, 0.03); }\n" +
                "        .modal { display: none; position: fixed; z-index: 1000; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.8); justify-content: center; align-items: center; backdrop-filter: blur(4px); }\n" +
                "        .modal-content { background: var(--card-bg); border: 1px solid var(--border-color); padding: 2rem; border-radius: 16px; width: 320px; text-align: center; animation: scaleUp 0.3s forwards; }\n" +
                "        .modal-content h3 { color: var(--text-white); margin-bottom: 1rem; }\n" +
                "        .form-control { width: 100%; padding: 0.6rem; margin-bottom: 1rem; background: var(--inner-card-bg); border: 1px solid var(--border-color); border-radius: 8px; color: var(--text-white); outline: none; }\n" +
                "        .btn-submit { width: 100%; background: var(--accent-cyan); border: none; padding: 0.6rem; border-radius: 8px; font-weight: 700; cursor: pointer; color: #000; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"top-bar\">" + headerBtn + "</div>\n" +
                "    <div class=\"layout\">\n" +
                "        <aside class=\"sidebar\">\n" +
                "            <img src=\"/static/foto.jpg\" alt=\"Fabrizzio Rojas\" class=\"profile-img\" onerror=\"this.src='https://via.placeholder.com/110'\">\n" +
                "            <h1 class=\"profile-name\">Fabrizzio Ricardo Rojas Poma</h1>\n" +
                "            <div class=\"tag-dev\">&lt;Desarrollador Web /&gt;</div>\n" +
                "            <div class=\"sidebar-info\">\n" +
                "                <div>📍 Huancayo, Perú</div>\n" +
                "                <div>🏛️ IESTP A.A.C.D.</div>\n" +
                "                <div>💼 E-Portafolio 2026</div>\n" +
                "            </div>\n" +
                "        </aside>\n" +
                "        <main class=\"main-content\">\n" +
                "            <section class=\"card\">\n" +
                "                <h2 class=\"section-title\">Sobre Mí</h2>\n" +
                "                <p>Estudiante del <strong>IESTP Andrés Avelino Cáceres Dorregaray</strong> en Huancayo. Apasionado por la tecnología, el diseño web moderno y la construcción de aplicaciones dinámicas.</p>\n" +
                "            </section>\n" +
                "            <section class=\"card\">\n" +
                "                <h2 class=\"section-title\">Habilidades & Stack Tecnológico</h2>\n" +
                "                <div class=\"tech-grid\">\n" +
                "                    <div class=\"tech-box\">\n" +
                "                        <div class=\"tech-box-title\">Desarrollo & Backend</div>\n" +
                "                        <div class=\"badges-container\">\n" +
                "                            <span class=\"badge-item\">Java</span><span class=\"badge-item\">PHP</span><span class=\"badge-item\">MySQL</span>\n" +
                "                        </div>\n" +
                "                    </div>\n" +
                "                    <div class=\"tech-box\">\n" +
                "                        <div class=\"tech-box-title\">Gestión & Plataformas</div>\n" +
                "                        <div class=\"badges-container\">\n" +
                "                            <span class=\"badge-item\">GitHub</span><span class=\"badge-item\">Trello</span><span class=\"badge-item\">Jira</span><span class=\"badge-item\">WordPress</span><span class=\"badge-item\">Netlify</span><span class=\"badge-item\">InfinityFree</span>\n" +
                "                        </div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            </section>\n" +
                "            <section class=\"card\">\n" +
                "                <h2 class=\"section-title\">Módulos de Evidencias</h2>\n" +
                "                <div style=\"margin-bottom: 1.2rem;\">\n" +
                "                    <input type=\"text\" id=\"buscadorEvidencias\" placeholder=\"🔍 Buscar por semana o descripción...\" onkeyup=\"filtrarEvidencias()\" style=\"width: 100%; padding: 0.7rem 1rem; background: var(--inner-card-bg); border: 1px solid var(--border-color); border-radius: 8px; color: var(--text-white); outline: none; font-size: 0.88rem;\">\n" +
                "                </div>\n" +
                "                <div id=\"contenedorEvidencias\">\n" +
                "                    " + evidenciasHtml.toString() + "\n" +
                "                </div>\n" +
                "            </section>\n" +
                "        </main>\n" +
                "    </div>\n" +
                "    <div id=\"loginModal\" class=\"modal\">\n" +
                "        <div class=\"modal-content\">\n" +
                "            <h3>Acceso Alumno</h3>\n" +
                "            <form action=\"/login\" method=\"POST\">\n" +
                "                <input type=\"text\" name=\"usuario\" class=\"form-control\" placeholder=\"Usuario\" required>\n" +
                "                <input type=\"password\" name=\"password\" class=\"form-control\" placeholder=\"Contraseña\" required>\n" +
                "                <button type=\"submit\" class=\"btn-submit\">Ingresar</button>\n" +
                "            </form>\n" +
                "            <button onclick=\"cerrarModal()\" style=\"margin-top: 0.8rem; background: transparent; border: none; color: var(--text-muted); cursor: pointer; font-size: 0.8rem;\">Cancelar</button>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <script>\n" +
                "        function abrirModal() { document.getElementById('loginModal').style.display = 'flex'; }\n" +
                "        function cerrarModal() { document.getElementById('loginModal').style.display = 'none'; }\n" +
                "        function filtrarEvidencias() {\n" +
                "            let input = document.getElementById('buscadorEvidencias').value.toLowerCase();\n" +
                "            let items = document.querySelectorAll('#contenedorEvidencias details');\n" +
                "            items.forEach(item => {\n" +
                "                let texto = item.textContent.toLowerCase();\n" +
                "                if (texto.includes(input)) {\n" +
                "                    item.style.display = \"\";\n" +
                "                } else {\n" +
                "                    item.style.display = \"none\";\n" +
                "                }\n" +
                "            });\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

    public static String renderCPanel(List<Evidencia> listaEvidencias) {
        int totalSemanas = (listaEvidencias != null) ? (int) listaEvidencias.stream().map(Evidencia::getSemana).distinct().count() : 0;
        int totalTareas = (listaEvidencias != null) ? listaEvidencias.size() : 0;

        StringBuilder listaAdmin = new StringBuilder();
        if (listaEvidencias == null || listaEvidencias.isEmpty()) {
            listaAdmin.append("<p style=\"font-size: 0.88rem; color: var(--text-muted);\">No hay semanas registradas.</p>");
        } else {
            for (Evidencia ev : listaEvidencias) {
                String adminItem = String.format(
                        "<div style=\"background: var(--bg-main); border: 1px solid var(--border-color); border-radius: 10px; padding: 1rem; margin-bottom: 0.8rem; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;\">" +
                                "   <div>" +
                                "       <span style=\"color: var(--accent-cyan); font-weight: 600; font-size: 0.88rem;\">📌 %s</span>" +
                                "       <p style=\"font-size: 0.85rem; color: var(--text-white); margin-top: 0.3rem;\">%s</p>" +
                                "   </div>" +
                                "   <div style=\"display: flex; gap: 0.5rem;\">" +
                                "       <button onclick=\"abrirEditar('%s', '%s', '%s')\" style=\"background: rgba(0, 242, 254, 0.1); border: 1px solid var(--accent-cyan); color: var(--accent-cyan); padding: 0.35rem 0.8rem; border-radius: 6px; cursor: pointer; font-size: 0.8rem; font-weight: 600;\">Editar</button>" +
                                "       <form action=\"/eliminar-trabajo\" method=\"POST\" style=\"display:inline;\">" +
                                "           <input type=\"hidden\" name=\"id\" value=\"%s\">" +
                                "           <button type=\"submit\" style=\"background: rgba(255, 77, 77, 0.1); border: 1px solid #ff4d4d; color: #ff4d4d; padding: 0.35rem 0.8rem; border-radius: 6px; cursor: pointer; font-size: 0.8rem; font-weight: 600;\">Eliminar</button>" +
                                "       </form>" +
                                "   </div>" +
                                "</div>",
                        escapeHtml(ev.getSemana()), escapeHtml(ev.getDescripcion()), ev.getId(), escapeHtml(ev.getSemana()), escapeHtml(ev.getDescripcion()), ev.getId()
                );
                listaAdmin.append(adminItem);
            }
        }

        return "<!DOCTYPE html>\n" +
                "<html lang=\"es\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Panel de Control - Console Admin</title>\n" +
                "    <style>\n" +
                "        :root { --bg-main: #060913; --card-bg: #0d1322; --border-color: #172033; --accent-cyan: #00f2fe; --text-white: #ffffff; --text-muted: #94a3b8; }\n" +
                "        * { box-sizing: border-box; margin: 0; padding: 0; }\n" +
                "        @keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }\n" +
                "        @keyframes scaleUp { from { opacity: 0; transform: scale(0.95); } to { opacity: 1; transform: scale(1); } }\n" +
                "        body { font-family: 'Segoe UI', system-ui, sans-serif; background: var(--bg-main); color: var(--text-muted); display: flex; min-height: 100vh; animation: fadeIn 0.4s ease-out; }\n" +
                "        \n" +
                "        /* Sidebar */\n" +
                "        .sidebar { width: 260px; background: var(--card-bg); border-right: 1px solid var(--border-color); display: flex; flex-direction: column; justify-content: space-between; padding: 1.5rem; position: fixed; height: 100vh; }\n" +
                "        .sidebar-top { display: flex; flex-direction: column; gap: 1.5rem; }\n" +
                "        .sidebar-brand { color: var(--text-white); font-size: 1.1rem; font-weight: 700; display: flex; align-items: center; gap: 0.6rem; }\n" +
                "        .sidebar-menu { display: flex; flex-direction: column; gap: 0.4rem; }\n" +
                "        .menu-item { display: flex; align-items: center; gap: 0.6rem; padding: 0.7rem 1rem; border-radius: 8px; color: var(--text-muted); text-decoration: none; font-size: 0.9rem; font-weight: 500; transition: all 0.2s; }\n" +
                "        .menu-item.active, .menu-item:hover { background: rgba(0, 242, 254, 0.1); color: var(--accent-cyan); border: 1px solid rgba(0, 242, 254, 0.2); }\n" +
                "        .btn-logout { display: flex; align-items: center; gap: 0.6rem; color: #ff4d4d; text-decoration: none; font-size: 0.9rem; font-weight: 600; padding: 0.6rem 1rem; border-radius: 8px; border: 1px solid rgba(255, 77, 77, 0.2); background: rgba(255, 77, 77, 0.05); }\n" +
                "        \n" +
                "        /* Main Dashboard */\n" +
                "        .main-container { margin-left: 260px; flex: 1; padding: 2rem; max-width: calc(100vw - 260px); }\n" +
                "        .page-title { color: var(--text-white); font-size: 1.5rem; font-weight: 700; margin-bottom: 1.5rem; }\n" +
                "        \n" +
                "        /* Metrics Grid */\n" +
                "        .metrics-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 1rem; margin-bottom: 1.5rem; }\n" +
                "        .metric-card { background: var(--card-bg); border: 1px solid var(--border-color); border-radius: 12px; padding: 1.2rem 1.5rem; }\n" +
                "        .metric-title { font-size: 0.75rem; font-weight: 700; text-transform: uppercase; color: var(--text-muted); letter-spacing: 0.5px; margin-bottom: 0.4rem; }\n" +
                "        .metric-value { color: var(--text-white); font-size: 1.8rem; font-weight: 700; }\n" +
                "        \n" +
                "        /* Cards / Sections */\n" +
                "        .card { background: var(--card-bg); border: 1px solid var(--border-color); border-radius: 16px; padding: 1.8rem; margin-bottom: 1.5rem; }\n" +
                "        .card-title { color: var(--text-white); font-size: 1.1rem; font-weight: 700; margin-bottom: 1.2rem; display: flex; align-items: center; gap: 0.5rem; }\n" +
                "        \n" +
                "        .form-group { margin-bottom: 1rem; }\n" +
                "        .form-group label { display: block; color: var(--text-white); font-size: 0.85rem; font-weight: 600; margin-bottom: 0.4rem; }\n" +
                "        .form-control { width: 100%; padding: 0.7rem 1rem; background: #080d1a; border: 1px solid var(--border-color); border-radius: 8px; color: var(--text-white); outline: none; font-size: 0.9rem; }\n" +
                "        .btn-submit { background: var(--accent-cyan); color: #000; border: none; padding: 0.7rem 1.4rem; border-radius: 8px; font-weight: 700; cursor: pointer; font-size: 0.9rem; }\n" +
                "        \n" +
                "        /* Modal */\n" +
                "        .modal { display: none; position: fixed; z-index: 1000; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.8); justify-content: center; align-items: center; backdrop-filter: blur(4px); }\n" +
                "        .modal-content { background: var(--card-bg); border: 1px solid var(--border-color); padding: 2rem; border-radius: 16px; width: 400px; animation: scaleUp 0.3s forwards; }\n" +
                "        .modal-content h3 { color: var(--text-white); margin-bottom: 1rem; font-size: 1.1rem; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <aside class=\"sidebar\">\n" +
                "        <div class=\"sidebar-top\">\n" +
                "            <div class=\"sidebar-brand\">🛡️ Console Admin</div>\n" +
                "            <div class=\"sidebar-menu\">\n" +
                "                <a href=\"/cpanel\" class=\"menu-item active\">📊 Dashboard</a>\n" +
                "                <a href=\"/\" class=\"menu-item\">🌐 Ver Portafolio</a>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <div>\n" +
                "            <a href=\"/logout\" class=\"btn-logout\">🚪 Cerrar Sesión</a>\n" +
                "        </div>\n" +
                "    </aside>\n" +
                "\n" +
                "    <div class=\"main-container\">\n" +
                "        <h1 class=\"page-title\">Panel de Control</h1>\n" +
                "        \n" +
                "        <div class=\"metrics-grid\">\n" +
                "            <div class=\"metric-card\">\n" +
                "                <div class=\"metric-title\">Total Semanas</div>\n" +
                "                <div class=\"metric-value\">" + totalSemanas + "</div>\n" +
                "            </div>\n" +
                "            <div class=\"metric-card\">\n" +
                "                <div class=\"metric-title\">Semanas Completadas</div>\n" +
                "                <div class=\"metric-value\">" + totalSemanas + "</div>\n" +
                "            </div>\n" +
                "            <div class=\"metric-card\">\n" +
                "                <div class=\"metric-title\">Total Tareas Subidas</div>\n" +
                "                <div class=\"metric-value\">" + totalTareas + "</div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "\n" +
                "        <div class=\"card\">\n" +
                "            <div class=\"card-title\">📝 Registrar Tarea Académica</div>\n" +
                "            <form action=\"/subir-trabajo\" method=\"POST\" enctype=\"multipart/form-data\">\n" +
                "                <div class=\"form-group\">\n" +
                "                    <label>Título / Semana Destino:</label>\n" +
                "                    <input type=\"text\" name=\"semana\" class=\"form-control\" placeholder=\"Ej. Semana 3\" required>\n" +
                "                </div>\n" +
                "                <div class=\"form-group\">\n" +
                "                    <label>Descripción del Trabajo:</label>\n" +
                "                    <input type=\"text\" name=\"descripcion\" class=\"form-control\" placeholder=\"Ej. Infografía interactiva...\" required>\n" +
                "                </div>\n" +
                "                <div class=\"form-group\">\n" +
                "                    <label>Archivo PDF (Opcional):</label>\n" +
                "                    <input type=\"file\" name=\"pdfFile\" accept=\"application/pdf\" class=\"form-control\">\n" +
                "                </div>\n" +
                "                <button type=\"submit\" class=\"btn-submit\">Crear y Publicar</button>\n" +
                "            </form>\n" +
                "        </div>\n" +
                "\n" +
                "        <div class=\"card\">\n" +
                "            <div class=\"card-title\">📂 Administrar Semanas Publicadas</div>\n" +
                "            <div>\n" +
                "                " + listaAdmin.toString() + "\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "\n" +
                "    <div id=\"editModal\" class=\"modal\">\n" +
                "        <div class=\"modal-content\">\n" +
                "            <h3>Editar Semana / Trabajo</h3>\n" +
                "            <form action=\"/editar-trabajo\" method=\"POST\" enctype=\"multipart/form-data\">\n" +
                "                <input type=\"hidden\" name=\"id\" id=\"edit-id\">\n" +
                "                <div class=\"form-group\">\n" +
                "                    <label>Título / Semana:</label>\n" +
                "                    <input type=\"text\" name=\"semana\" id=\"edit-semana\" class=\"form-control\" required>\n" +
                "                </div>\n" +
                "                <div class=\"form-group\">\n" +
                "                    <label>Descripción:</label>\n" +
                "                    <input type=\"text\" name=\"descripcion\" id=\"edit-descripcion\" class=\"form-control\" required>\n" +
                "                </div>\n" +
                "                <div class=\"form-group\">\n" +
                "                    <label>Reemplazar o Adjuntar PDF:</label>\n" +
                "                    <input type=\"file\" name=\"pdfFile\" accept=\"application/pdf\" class=\"form-control\">\n" +
                "                </div>\n" +
                "                <button type=\"submit\" class=\"btn-submit\" style=\"width: 100%; margin-top: 0.5rem;\">Guardar Cambios</button>\n" +
                "            </form>\n" +
                "            <button onclick=\"cerrarEditar()\" style=\"margin-top: 0.8rem; background: transparent; border: none; color: var(--text-muted); cursor: pointer; font-size: 0.8rem; width:100%;\">Cancelar</button>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "\n" +
                "    <script>\n" +
                "        function abrirEditar(id, semana, descripcion) {\n" +
                "            document.getElementById('edit-id').value = id;\n" +
                "            document.getElementById('edit-semana').value = semana;\n" +
                "            document.getElementById('edit-descripcion').value = descripcion;\n" +
                "            document.getElementById('editModal').style.display = 'flex';\n" +
                "        }\n" +
                "        function cerrarEditar() { document.getElementById('editModal').style.display = 'none'; }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

    public static String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}