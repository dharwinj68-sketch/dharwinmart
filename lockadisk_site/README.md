# LockADisk Landing Page

This directory contains a simple static website for **LockADisk**.

## Files
- `index.html` – HTML markup for the landing page.
- `style.css` – Basic styling.
- `README.md` – This file.

## How to view locally
You can open `index.html` directly in a browser, but for a proper local server (e.g., to handle relative links correctly) you can use Python's built‑in HTTP server:

```bash
# From this directory
python -m http.server 8000
```

Then open your browser and navigate to:

```
http://localhost:8000
```

The site will be served on port 8000. Press `Ctrl+C` to stop the server.

## Customisation
- Update the hero image by replacing `hero-bg.jpg` (add the image to this folder).
- Edit the download links in the *Download* section to point to real installer files.
- Extend the CSS in `style.css` to match your branding.
