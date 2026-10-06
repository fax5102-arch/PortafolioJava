package com.portafolio.view;

import com.portafolio.model.Evidencia;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class ViewHtml {

    public static String renderPortafolio(List<Evidencia> listaEvidencias, boolean autenticado) {
        StringBuilder evidenciasHtml = new StringBuilder();
        if (listaEvidencias == null || listaEvidencias.isEmpty()) {
            evidenciasHtml.append("<p style=\"font-size: 0.9rem; color: var(--text-muted); text-align: center; padding: 2rem; grid-column: 1 / -1;\">No hay evidencias publicadas aún.</p>");
        } else {
            Map<String, List<Evidencia>> evidenciasPorSemana = listaEvidencias.stream()
                    .collect(Collectors.groupingBy(Evidencia::getSemana, LinkedHashMap::new, Collectors.toList()));

            for (Map.Entry<String, List<Evidencia>> entry : evidenciasPorSemana.entrySet()) {
                String semana = entry.getKey();
                List<Evidencia> trabajos = entry.getValue();
                int totalEvidencias = trabajos.size();

                StringBuilder trabajosHtml = new StringBuilder();
                for (Evidencia ev : trabajos) {
                    String pdfBtn = (ev.getPdfUrl() != null && !ev.getPdfUrl().isEmpty())
                            ? String.format("<a href=\"%s\" target=\"_blank\" class=\"btn-pdf\">Ver PDF ›</a>", escapeHtml(ev.getPdfUrl()))
                            : "<span style=\"font-size: 0.75rem; color: var(--text-muted); font-style: italic;\">Sin archivo</span>";

                    trabajosHtml.append(String.format(
                            "<div class=\"evidencia-item\">" +
                                    "<p class=\"evidencia-desc\">%s</p>" +
                                    "%s" +
                                    "</div>",
                            escapeHtml(ev.getDescripcion()), pdfBtn
                    ));
                }

                String cardHtml = String.format(
                        "<div class=\"semana-card\">" +
                                "   <div class=\"semana-card-header\">" +
                                "       <span class=\"semana-badge\">%s</span>" +
                                "       <span style=\"font-size: 1.1rem;\">📂</span>" +
                                "   </div>" +
                                "   <h3 class=\"semana-card-title\">%s</h3>" +
                                "   <p class=\"semana-card-sub\">Haz clic en el botón inferior para revisar las evidencias de esta sesión.</p>" +
                                "   <div class=\"semana-card-meta\">" +
                                "       <span>📄 %d evidencia(s) subida(s)</span>" +
                                "   </div>" +
                                "   <details class=\"semana-details\">" +
                                "       <summary class=\"semana-summary\">+ Abrir contenido</summary>" +
                                "       <div class=\"semana-content\">" +
                                "           %s" +
                                "       </div>" +
                                "   </details>" +
                                "</div>",
                        escapeHtml(semana), escapeHtml(semana), totalEvidencias, trabajosHtml.toString()
                );
                evidenciasHtml.append(cardHtml);
            }
        }

        String navAuthAction = autenticado
                ? "<a href=\"/cpanel\" class=\"btn-cpanel\">⚙️ Ir a cPanel</a> <a href=\"/logout\" class=\"btn-login-nav\">Cerrar Sesión</a>"
                : "<button onclick=\"abrirModal()\" class=\"btn-login-nav\">🔑 Acceso Alumno</button>";

        return "<!DOCTYPE html>\n" +
                "<html lang=\"es\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Fabrizzio Rojas | Portafolio Académico</title>\n" +
                "    <style>\n" +
                "        :root {\n" +
                "            --bg-main: #060913; --card-bg: #0d1322; --inner-card-bg: #080d1a;\n" +
                "            --border-color: #172033; --accent-cyan: #00f2fe; --text-white: #ffffff; --text-muted: #94a3b8;\n" +
                "        }\n" +
                "        * { box-sizing: border-box; margin: 0; padding: 0; scroll-behavior: smooth; }\n" +
                "        @keyframes fadeIn { from { opacity: 0; transform: translateY(15px); } to { opacity: 1; transform: translateY(0); } }\n" +
                "        @keyframes scaleUp { from { opacity: 0; transform: scale(0.92); } to { opacity: 1; transform: scale(1); } }\n" +
                "        body {\n" +
                "            font-family: 'Segoe UI', system-ui, sans-serif; background-color: var(--bg-main); color: var(--text-muted);\n" +
                "            min-height: 100vh; animation: fadeIn 0.6s ease-out; padding-top: 80px;\n" +
                "        }\n" +
                "        .navbar {\n" +
                "            position: fixed; top: 0; left: 0; width: 100%; z-index: 100;\n" +
                "            background: rgba(6, 9, 19, 0.85); backdrop-filter: blur(12px);\n" +
                "            border-bottom: 1px solid var(--border-color); padding: 1rem 2rem;\n" +
                "            display: flex; justify-content: space-between; align-items: center;\n" +
                "        }\n" +
                "        .brand-logo { color: var(--text-white); font-weight: 800; font-size: 1.15rem; text-decoration: none; letter-spacing: -0.5px; }\n" +
                "        .brand-logo span { color: var(--accent-cyan); }\n" +
                "        .nav-links { display: flex; align-items: center; gap: 1.8rem; list-style: none; }\n" +
                "        .nav-links a { color: var(--text-muted); text-decoration: none; font-size: 0.85rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; transition: color 0.3s ease; }\n" +
                "        .nav-links a:hover { color: var(--accent-cyan); }\n" +
                "        .btn-login-nav, .btn-cpanel {\n" +
                "            background: transparent; border: 1px solid var(--accent-cyan); color: var(--accent-cyan);\n" +
                "            padding: 0.45rem 1rem; border-radius: 20px; font-size: 0.82rem; font-weight: 600; cursor: pointer; text-decoration: none;\n" +
                "            transition: all 0.3s ease;\n" +
                "        }\n" +
                "        .btn-login-nav:hover, .btn-cpanel:hover { background: rgba(0, 242, 254, 0.15); transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0, 242, 254, 0.2); }\n" +
                "        .container { max-width: 1150px; margin: 0 auto; padding: 2rem 1.5rem; }\n" +
                "        .hero-section { display: grid; grid-template-columns: 1.2fr 0.8fr; gap: 2.5rem; align-items: center; margin-bottom: 3.5rem; padding: 2rem 0; }\n" +
                "        .tag-academic { display: inline-block; background: rgba(0, 242, 254, 0.08); color: var(--accent-cyan); border: 1px solid rgba(0, 242, 254, 0.25); padding: 0.35rem 0.9rem; border-radius: 20px; font-size: 0.78rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.8px; margin-bottom: 1.2rem; }\n" +
                "        .hero-title { color: var(--text-white); font-size: 2.7rem; font-weight: 800; line-height: 1.15; margin-bottom: 1.2rem; letter-spacing: -1px; }\n" +
                "        .hero-title span { color: var(--accent-cyan); }\n" +
                "        .hero-desc { font-size: 0.98rem; line-height: 1.6; color: var(--text-muted); margin-bottom: 2rem; }\n" +
                "        .hero-desc strong { color: var(--text-white); }\n" +
                "        .hero-buttons { display: flex; gap: 1rem; flex-wrap: wrap; }\n" +
                "        .btn-primary { background: var(--accent-cyan); color: #060913; font-weight: 700; padding: 0.75rem 1.6rem; border-radius: 30px; text-decoration: none; font-size: 0.9rem; transition: all 0.3s ease; border: none; }\n" +
                "        .btn-primary:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(0, 242, 254, 0.3); }\n" +
                "        .btn-secondary { background: transparent; color: var(--text-white); font-weight: 600; padding: 0.75rem 1.6rem; border-radius: 30px; text-decoration: none; font-size: 0.9rem; border: 1px solid var(--border-color); transition: all 0.3s ease; }\n" +
                "        .btn-secondary:hover { border-color: var(--accent-cyan); color: var(--accent-cyan); transform: translateY(-2px); }\n" +
                "        .hero-image-wrapper { position: relative; display: flex; justify-content: center; align-items: center; }\n" +
                "        .hero-card-frame { background: var(--card-bg); border: 1px solid var(--border-color); border-radius: 24px; padding: 1.8rem 1.5rem; text-align: center; width: 100%; max-width: 330px; box-shadow: 0 20px 40px rgba(0,0,0,0.4); position: relative; overflow: hidden; }\n" +
                "        .hero-card-frame::before { content: ''; position: absolute; top: 0; left: 0; width: 100%; height: 4px; background: linear-gradient(90deg, transparent, var(--accent-cyan), transparent); }\n" +
                "        .profile-img { width: 140px; height: 140px; border-radius: 50%; object-fit: cover; border: 3px solid var(--accent-cyan); margin-bottom: 1rem; }\n" +
                "        .profile-name { color: var(--text-white); font-size: 1.15rem; font-weight: 700; margin-bottom: 0.3rem; }\n" +
                "        .profile-role { color: var(--accent-cyan); font-size: 0.8rem; font-family: monospace; font-weight: 600; margin-bottom: 0.8rem; }\n" +
                "        .section-card { background: var(--card-bg); border: 1px solid var(--border-color); border-radius: 20px; padding: 2rem; margin-bottom: 2.5rem; transition: transform 0.3s ease; }\n" +
                "        .section-title { color: var(--text-white); font-size: 1.3rem; font-weight: 700; margin-bottom: 1.2rem; display: flex; align-items: center; }\n" +
                "        .section-title::before { content: ''; display: inline-block; width: 4px; height: 20px; background: var(--accent-cyan); margin-right: 0.7rem; border-radius: 2px; }\n" +
                "        .tech-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-top: 0.5rem; }\n" +
                "        .tech-box { background: var(--inner-card-bg); border: 1px solid var(--border-color); border-radius: 12px; padding: 1.2rem; }\n" +
                "        .tech-box-title { color: var(--accent-cyan); font-size: 0.75rem; font-weight: 700; text-transform: uppercase; margin-bottom: 0.8rem; letter-spacing: 0.5px; }\n" +
                "        .badges-container { display: flex; flex-wrap: wrap; gap: 0.5rem; }\n" +
                "        .badge-item { background: rgba(255, 255, 255, 0.04); color: var(--text-white); border: 1px solid var(--border-color); padding: 0.35rem 0.75rem; border-radius: 6px; font-size: 0.8rem; }\n" +
                "        .projects-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 1.2rem; }\n" +
                "        .project-card { background: var(--inner-card-bg); border: 1px solid var(--border-color); border-radius: 14px; padding: 1.4rem; transition: all 0.3s ease; }\n" +
                "        .project-card:hover { border-color: rgba(0, 242, 254, 0.4); transform: translateY(-3px); }\n" +
                "        .project-title { color: var(--text-white); font-size: 1.05rem; font-weight: 700; margin-bottom: 0.5rem; }\n" +
                "        .project-desc { font-size: 0.85rem; color: var(--text-muted); line-height: 1.5; margin-bottom: 1rem; }\n" +
                "\n" +
                "        /* Estilos de Tarjetas en Cuadrícula para las Semanas */\n" +
                "        .semanas-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 1.2rem; margin-top: 1rem; }\n" +
                "        .semana-card { background: var(--inner-card-bg); border: 1px solid var(--border-color); border-radius: 16px; padding: 1.4rem; display: flex; flex-direction: column; justify-content: space-between; transition: all 0.3s ease; position: relative; }\n" +
                "        .semana-card:hover { border-color: var(--accent-cyan); transform: translateY(-4px); box-shadow: 0 10px 25px rgba(0, 242, 254, 0.1); }\n" +
                "        .semana-card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.8rem; }\n" +
                "        .semana-badge { background: rgba(0, 242, 254, 0.12); color: var(--accent-cyan); border: 1px solid rgba(0, 242, 254, 0.3); font-size: 0.72rem; font-weight: 700; padding: 0.25rem 0.7rem; border-radius: 20px; text-transform: uppercase; }\n" +
                "        .semana-card-title { color: var(--text-white); font-size: 1.15rem; font-weight: 800; margin-bottom: 0.4rem; }\n" +
                "        .semana-card-sub { font-size: 0.8rem; color: var(--text-muted); line-height: 1.4; margin-bottom: 1rem; flex-grow: 1; }\n" +
                "        .semana-card-meta { font-size: 0.78rem; color: var(--accent-cyan); margin-bottom: 1rem; padding-top: 0.6rem; border-top: 1px dashed var(--border-color); font-weight: 600; }\n" +
                "        .semana-details summary { cursor: pointer; color: var(--accent-cyan); font-size: 0.82rem; font-weight: 700; text-align: center; padding: 0.5rem; background: rgba(0, 242, 254, 0.05); border: 1px solid rgba(0, 242, 254, 0.2); border-radius: 8px; list-style: none; transition: all 0.2s; }\n" +
                "        .semana-details summary:hover { background: rgba(0, 242, 254, 0.15); }\n" +
                "        .semana-content { margin-top: 0.8rem; display: flex; flex-direction: column; gap: 0.6rem; }\n" +
                "        .evidencia-item { background: var(--bg-main); border: 1px solid var(--border-color); border-radius: 8px; padding: 0.7rem; }\n" +
                "        .evidencia-desc { font-size: 0.8rem; color: var(--text-white); margin-bottom: 0.4rem; font-weight: 500; }\n" +
                "        .btn-pdf { display: inline-flex; align-items: center; gap: 0.3rem; background: linear-gradient(90deg, #00c6ff 0%, #0072ff 100%); color: #fff; font-weight: 600; padding: 0.35rem 0.7rem; border-radius: 6px; text-decoration: none; font-size: 0.75rem; }\n" +
                "\n" +
                "        /* Modal Login */\n" +
                "        .modal { display: none; position: fixed; z-index: 1000; top: 0; left: 0; width: 100%; height: 100%; background: rgba(3, 5, 12, 0.88); justify-content: center; align-items: center; backdrop-filter: blur(8px); }\n" +
                "        .modal-card { background: var(--card-bg); border: 1px solid var(--border-color); border-radius: 28px; width: 380px; overflow: hidden; box-shadow: 0 25px 50px rgba(0, 0, 0, 0.6); animation: scaleUp 0.3s ease-out; position: relative; }\n" +
                "        .modal-close-btn { position: absolute; top: 12px; right: 14px; background: rgba(0,0,0,0.4); border: 1px solid var(--border-color); color: #fff; width: 30px; height: 30px; border-radius: 50%; display: flex; align-items: center; justify-content: center; cursor: pointer; font-size: 0.9rem; z-index: 10; transition: all 0.2s; }\n" +
                "        .modal-close-btn:hover { background: rgba(255, 77, 77, 0.2); border-color: #ff4d4d; color: #ff4d4d; }\n" +
                "        .modal-header-banner { background: linear-gradient(135deg, #09132b 0%, #003853 100%); height: 130px; display: flex; flex-direction: column; justify-content: center; align-items: center; border-bottom: 1px solid var(--border-color); position: relative; }\n" +
                "        .modal-header-icon { font-size: 2.8rem; filter: drop-shadow(0 4px 10px rgba(0, 242, 254, 0.3)); }\n" +
                "        .modal-body { padding: 1.8rem 2rem 2.2rem; text-align: center; }\n" +
                "        .modal-welcome-title { color: var(--text-white); font-size: 1.5rem; font-weight: 800; margin-bottom: 0.2rem; letter-spacing: -0.5px; }\n" +
                "        .modal-welcome-sub { font-size: 0.82rem; color: var(--text-muted); margin-bottom: 1.5rem; }\n" +
                "        .input-group-custom { position: relative; margin-bottom: 1.1rem; text-align: left; }\n" +
                "        .input-icon { position: absolute; left: 1rem; top: 50%; transform: translateY(-50%); color: var(--accent-cyan); font-size: 0.95rem; opacity: 0.8; }\n" +
                "        .form-input-pill { width: 100%; padding: 0.75rem 1rem 0.75rem 2.7rem; background: var(--inner-card-bg); border: 1px solid var(--border-color); border-radius: 30px; color: var(--text-white); outline: none; font-size: 0.88rem; transition: all 0.3s ease; }\n" +
                "        .form-input-pill:focus { border-color: var(--accent-cyan); box-shadow: 0 0 12px rgba(0, 242, 254, 0.25); background: #060a17; }\n" +
                "        .btn-login-gradient { width: 100%; background: linear-gradient(90deg, #00f2fe 0%, #00a8ff 100%); border: none; padding: 0.8rem; border-radius: 30px; font-weight: 800; cursor: pointer; color: #060913; font-size: 0.95rem; letter-spacing: 0.3px; transition: all 0.3s ease; margin-top: 0.5rem; box-shadow: 0 6px 20px rgba(0, 242, 254, 0.25); }\n" +
                "        .btn-login-gradient:hover { transform: translateY(-2px); box-shadow: 0 8px 25px rgba(0, 242, 254, 0.4); }\n" +
                "        .modal-footer-note { margin-top: 1.4rem; font-size: 0.75rem; color: var(--text-muted); border-top: 1px dashed var(--border-color); padding-top: 1rem; }\n" +
                "        \n" +
                "        @media (max-width: 850px) {\n" +
                "            .hero-section { grid-template-columns: 1fr; text-align: center; }\n" +
                "            .hero-title { font-size: 2.1rem; }\n" +
                "            .hero-buttons { justify-content: center; }\n" +
                "            .hero-image-wrapper { margin-top: 1rem; }\n" +
                "            .tech-grid { grid-template-columns: 1fr; }\n" +
                "            .nav-links { display: none; }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <header class=\"navbar\">\n" +
                "        <a href=\"#inicio\" class=\"brand-logo\">Fabrizzio<span>RP</span></a>\n" +
                "        <ul class=\"nav-links\">\n" +
                "            <li><a href=\"#inicio\">Inicio</a></li>\n" +
                "            <li><a href=\"#sobre-mi\">Sobre Mí</a></li>\n" +
                "            <li><a href=\"#proyectos\">Proyectos</a></li>\n" +
                "            <li><a href=\"#semanas\">Semanas</a></li>\n" +
                "        </ul>\n" +
                "        <div>" + navAuthAction + "</div>\n" +
                "    </header>\n" +
                "\n" +
                "    <main class=\"container\">\n" +
                "        <section id=\"inicio\" class=\"hero-section\">\n" +
                "            <div>\n" +
                "                <span class=\"tag-academic\">⚡ PORTAFOLIO ACADÉMICO</span>\n" +
                "                <h1 class=\"hero-title\">Diseño ideas y las convierto en <span>experiencias web.</span></h1>\n" +
                "                <p class=\"hero-desc\">\n" +
                "                    Soy <strong>Fabrizzio Ricardo Rojas Poma</strong>, estudiante de la carrera de <strong>Diseño y Programación Web</strong> en el <strong>IESTP Andrés Avelino Cáceres Dorregaray</strong>. Este portafolio reúne mi proceso de aprendizaje, actividades, evidencias y proyectos desarrollados en el curso.\n" +
                "                </p>\n" +
                "                <div class=\"hero-buttons\">\n" +
                "                    <a href=\"#semanas\" class=\"btn-primary\">Ver semanas →</a>\n" +
                "                    <a href=\"#proyectos\" class=\"btn-secondary\">Explorar proyectos</a>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            <div class=\"hero-image-wrapper\">\n" +
                "                <div class=\"hero-card-frame\">\n" +
                "                    <img src=\"/static/foto.jpg\" alt=\"Fabrizzio Rojas\" class=\"profile-img\" onerror=\"this.src='https://via.placeholder.com/140'\">\n" +
                "                    <h2 class=\"profile-name\">Fabrizzio Rojas Poma</h2>\n" +
                "                    <div class=\"profile-role\">&lt;Desarrollador Web /&gt;</div>\n" +
                "                    <p style=\"font-size: 0.8rem; color: var(--text-muted);\">📍 Huancayo, Perú</p>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </section>\n" +
                "\n" +
                "        <section id=\"sobre-mi\" class=\"section-card\">\n" +
                "            <h2 class=\"section-title\">Sobre Mí</h2>\n" +
                "            <p style=\"line-height: 1.6;\">Estudiante apasionado por el desarrollo de software, la arquitectura web moderna y la construcción de aplicaciones interactivas. Enfocado en aplicar buenas prácticas, integración con bases de datos SQL / Cloud y optimización de experiencia de usuario.</p>\n" +
                "        </section>\n" +
                "\n" +
                "        <section class=\"section-card\">\n" +
                "            <h2 class=\"section-title\">Habilidades & Stack Tecnológico</h2>\n" +
                "            <div class=\"tech-grid\">\n" +
                "                <div class=\"tech-box\">\n" +
                "                    <div class=\"tech-box-title\">Desarrollo & Backend</div>\n" +
                "                    <div class=\"badges-container\">\n" +
                "                        <span class=\"badge-item\">Java</span>\n" +
                "                        <span class=\"badge-item\">PHP</span>\n" +
                "                        <span class=\"badge-item\">SQLite / MySQL</span>\n" +
                "                        <span class=\"badge-item\">Supabase Cloud</span>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "                <div class=\"tech-box\">\n" +
                "                    <div class=\"tech-box-title\">Gestión & Plataformas</div>\n" +
                "                    <div class=\"badges-container\">\n" +
                "                        <span class=\"badge-item\">GitHub</span>\n" +
                "                        <span class=\"badge-item\">Trello</span>\n" +
                "                        <span class=\"badge-item\">Jira</span>\n" +
                "                        <span class=\"badge-item\">WordPress</span>\n" +
                "                        <span class=\"badge-item\">Netlify</span>\n" +
                "                        <span class=\"badge-item\">InfinityFree</span>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </section>\n" +
                "\n" +
                "        <section id=\"proyectos\" class=\"section-card\">\n" +
                "            <h2 class=\"section-title\">Proyectos Académicos</h2>\n" +
                "            <div class=\"projects-grid\">\n" +
                "                <div class=\"project-card\">\n" +
                "                    <div class=\"project-title\">🌐 E-Portafolio 2026</div>\n" +
                "                    <p class=\"project-desc\">Plataforma web desarrollada en Java con SQLite y almacenamiento de evidencias PDF en la nube con Supabase Storage.</p>\n" +
                "                    <span class=\"badge-item\" style=\"color: var(--accent-cyan); border-color: rgba(0,242,254,0.3);\">Java + Supabase</span>\n" +
                "                </div>\n" +
                "                <div class=\"project-card\">\n" +
                "                    <div class=\"project-title\">⚙️ Console Admin CPanel</div>\n" +
                "                    <p class=\"project-desc\">Panel de administración interno para gestión CRUD de semanas, cargas multipart de archivos y métricas.</p>\n" +
                "                    <span class=\"badge-item\" style=\"color: var(--accent-cyan); border-color: rgba(0,242,254,0.3);\">Custom HttpHandler</span>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </section>\n" +
                "\n" +
                "        <section id=\"semanas\" class=\"section-card\">\n" +
                "            <h2 class=\"section-title\">Módulos de Evidencias Académicas</h2>\n" +
                "            <div style=\"margin-bottom: 1.5rem;\">\n" +
                "                <input type=\"text\" id=\"buscadorEvidencias\" placeholder=\"🔍 Buscar por semana o descripción de tarea...\" onkeyup=\"filtrarEvidencias()\" style=\"width: 100%; padding: 0.8rem 1.2rem; background: var(--inner-card-bg); border: 1px solid var(--border-color); border-radius: 10px; color: var(--text-white); outline: none; font-size: 0.9rem;\">\n" +
                "            </div>\n" +
                "            <div id=\"contenedorEvidencias\" class=\"semanas-grid\">\n" +
                "                " + evidenciasHtml.toString() + "\n" +
                "            </div>\n" +
                "        </section>\n" +
                "    </main>\n" +
                "\n" +
                "    <!-- Modal Login -->\n" +
                "    <div id=\"loginModal\" class=\"modal\">\n" +
                "        <div class=\"modal-card\">\n" +
                "            <button class=\"modal-close-btn\" onclick=\"cerrarModal()\">✕</button>\n" +
                "            <div class=\"modal-header-banner\">\n" +
                "                <div class=\"modal-header-icon\">👨‍💻</div>\n" +
                "            </div>\n" +
                "            <div class=\"modal-body\">\n" +
                "                <h3 class=\"modal-welcome-title\">Welcome Back!</h3>\n" +
                "                <p class=\"modal-welcome-sub\">Inicia sesión para acceder al panel cPanel</p>\n" +
                "                <form action=\"/login\" method=\"POST\">\n" +
                "                    <div class=\"input-group-custom\">\n" +
                "                        <span class=\"input-icon\">👤</span>\n" +
                "                        <input type=\"text\" name=\"usuario\" class=\"form-input-pill\" placeholder=\"Usuario o Email\" required>\n" +
                "                    </div>\n" +
                "                    <div class=\"input-group-custom\">\n" +
                "                        <span class=\"input-icon\">🔒</span>\n" +
                "                        <input type=\"password\" name=\"password\" class=\"form-input-pill\" placeholder=\"Contraseña\" required>\n" +
                "                    </div>\n" +
                "                    <button type=\"submit\" class=\"btn-login-gradient\">Login</button>\n" +
                "                </form>\n" +
                "                <div class=\"modal-footer-note\">\n" +
                "                    Administración del Portafolio Académico 2026\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "\n" +
                "    <script>\n" +
                "        function abrirModal() { document.getElementById('loginModal').style.display = 'flex'; }\n" +
                "        function cerrarModal() { document.getElementById('loginModal').style.display = 'none'; }\n" +
                "        function filtrarEvidencias() {\n" +
                "            let input = document.getElementById('buscadorEvidencias').value.toLowerCase();\n" +
                "            let cards = document.querySelectorAll('#contenedorEvidencias .semana-card');\n" +
                "            cards.forEach(card => {\n" +
                "                let texto = card.textContent.toLowerCase();\n" +
                "                card.style.display = texto.includes(input) ? \"flex\" : \"none\";\n" +
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
                                "       <button onclick=\"abrirEditar(this)\" data-id=\"%s\" data-semana=\"%s\" data-descripcion=\"%s\" style=\"background: rgba(0, 242, 254, 0.1); border: 1px solid var(--accent-cyan); color: var(--accent-cyan); padding: 0.35rem 0.8rem; border-radius: 6px; cursor: pointer; font-size: 0.8rem; font-weight: 600;\">Editar</button>" +
                                "       <form action=\"/eliminar-trabajo\" method=\"POST\" style=\"display:inline;\">" +
                                "           <input type=\"hidden\" name=\"id\" value=\"%s\">" +
                                "           <button type=\"submit\" style=\"background: rgba(255, 77, 77, 0.1); border: 1px solid #ff4d4d; color: #ff4d4d; padding: 0.35rem 0.8rem; border-radius: 6px; cursor: pointer; font-size: 0.8rem; font-weight: 600;\">Eliminar</button>" +
                                "       </form>" +
                                "   </div>" +
                                "</div>",
                        escapeHtml(ev.getSemana()), escapeHtml(ev.getDescripcion()),
                        escapeHtml(ev.getId()), escapeHtml(ev.getSemana()), escapeHtml(ev.getDescripcion()),
                        escapeHtml(ev.getId())
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
                "        .sidebar { width: 260px; background: var(--card-bg); border-right: 1px solid var(--border-color); display: flex; flex-direction: column; justify-content: space-between; padding: 1.5rem; position: fixed; height: 100vh; }\n" +
                "        .sidebar-top { display: flex; flex-direction: column; gap: 1.5rem; }\n" +
                "        .sidebar-brand { color: var(--text-white); font-size: 1.1rem; font-weight: 700; display: flex; align-items: center; gap: 0.6rem; }\n" +
                "        .sidebar-menu { display: flex; flex-direction: column; gap: 0.4rem; }\n" +
                "        .menu-item { display: flex; align-items: center; gap: 0.6rem; padding: 0.7rem 1rem; border-radius: 8px; color: var(--text-muted); text-decoration: none; font-size: 0.9rem; font-weight: 500; transition: all 0.2s; }\n" +
                "        .menu-item.active, .menu-item:hover { background: rgba(0, 242, 254, 0.1); color: var(--accent-cyan); border: 1px solid rgba(0, 242, 254, 0.2); }\n" +
                "        .btn-logout { display: flex; align-items: center; gap: 0.6rem; color: #ff4d4d; text-decoration: none; font-size: 0.9rem; font-weight: 600; padding: 0.6rem 1rem; border-radius: 8px; border: 1px solid rgba(255, 77, 77, 0.2); background: rgba(255, 77, 77, 0.05); }\n" +
                "        .main-container { margin-left: 260px; flex: 1; padding: 2rem; max-width: calc(100vw - 260px); }\n" +
                "        .page-title { color: var(--text-white); font-size: 1.5rem; font-weight: 700; margin-bottom: 1.5rem; }\n" +
                "        .metrics-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 1rem; margin-bottom: 1.5rem; }\n" +
                "        .metric-card { background: var(--card-bg); border: 1px solid var(--border-color); border-radius: 12px; padding: 1.2rem 1.5rem; }\n" +
                "        .metric-title { font-size: 0.75rem; font-weight: 700; text-transform: uppercase; color: var(--text-muted); letter-spacing: 0.5px; margin-bottom: 0.4rem; }\n" +
                "        .metric-value { color: var(--text-white); font-size: 1.8rem; font-weight: 700; }\n" +
                "        .card { background: var(--card-bg); border: 1px solid var(--border-color); border-radius: 16px; padding: 1.8rem; margin-bottom: 1.5rem; }\n" +
                "        .card-title { color: var(--text-white); font-size: 1.1rem; font-weight: 700; margin-bottom: 1.2rem; display: flex; align-items: center; gap: 0.5rem; }\n" +
                "        .form-group { margin-bottom: 1rem; }\n" +
                "        .form-group label { display: block; color: var(--text-white); font-size: 0.85rem; font-weight: 600; margin-bottom: 0.4rem; }\n" +
                "        .form-control { width: 100%; padding: 0.7rem 1rem; background: #080d1a; border: 1px solid var(--border-color); border-radius: 8px; color: var(--text-white); outline: none; font-size: 0.9rem; }\n" +
                "        .btn-submit { background: var(--accent-cyan); color: #000; border: none; padding: 0.7rem 1.4rem; border-radius: 8px; font-weight: 700; cursor: pointer; font-size: 0.9rem; }\n" +
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
                "        function abrirEditar(btn) {\n" +
                "            document.getElementById('edit-id').value = btn.getAttribute('data-id');\n" +
                "            document.getElementById('edit-semana').value = btn.getAttribute('data-semana');\n" +
                "            document.getElementById('edit-descripcion').value = btn.getAttribute('data-descripcion');\n" +
                "            document.getElementById('editModal').style.display = 'flex';\n" +
                "        }\n" +
                "        function cerrarEditar() { document.getElementById('editModal').style.display = 'none'; }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

    public static String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}