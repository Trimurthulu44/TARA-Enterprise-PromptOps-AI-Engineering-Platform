from setuptools import setup, find_packages

setup(
    name="tara-ai",
    version="1.0.0",
    description="Official Python SDK for TARA PromptOps & AI Engineering Platform",
    packages=find_packages(),
    install_requires=[
        "requests>=2.28.0"
    ],
    python_requires=">=3.8",
)
