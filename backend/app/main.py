from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from dotenv import load_dotenv
from app.api.endpoints import chat, pdf, profile, reports
from app.services.logging_service import configure_logging

# Load .env file (GEMINI_API_KEY etc.)
load_dotenv()
configure_logging()

app = FastAPI(
    title="GirişimKolay AI Backend",
    description="Backend services for GirişimKolay MVP including Chat simulation and PDF Generation",
    version="1.0.0"
)

# Configure CORS for potential web testing
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(chat.router, prefix="/api/v1/chat", tags=["chat"])
app.include_router(profile.router, prefix="/api/v1/profile", tags=["profile"])
app.include_router(reports.router, prefix="/api/v1/reports", tags=["reports"])
app.include_router(pdf.router, prefix="/api/v1/pdf", tags=["pdf"])

@app.get("/")
def read_root():
    return {"message": "GirişimKolay API is running"}

if __name__ == "__main__":
    import uvicorn
    import os
    
    # Cloud Run, PORT çevresel değişkenini (environment variable) dinamik olarak atar.
    # Varsayılan portu 8080 olarak belirliyoruz.
    port = int(os.environ.get("PORT", 8080))
    
    # Cloud Run'da dışarıdan isteklere yanıt verebilmek için host "0.0.0.0" olmalıdır.
    uvicorn.run(
        "app.main:app",
        host="0.0.0.0",
        port=port,
        log_level="info",
        # Cloud Run container (yatay) olarak ölçeklendiği için genellikle 1 worker yeterlidir. 
        # Eğer çok çekirdekli (multi-CPU) bir Cloud Run instance kullanacaksanız bu değeri artırabilirsiniz.
        workers=1
    )
